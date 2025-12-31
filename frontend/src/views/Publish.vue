<template>
  <div>
    <!-- 顶部导航栏 -->
    <NavBar />

    <div class="community-page">
      <!-- 左侧导航栏 -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <h2 class="title">animal-shelter</h2>
          <p class="subtitle">分享社区</p>
        </div>

        <el-menu
          :default-active="isEditMode ? '3' : '2'"  
          background-color="transparent"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="1" @click="go('/sharing')">
            <el-icon><Promotion /></el-icon>
            <span>发现</span>
          </el-menu-item>

          <el-menu-item index="2" @click="go('/community/publish')" v-if="!isEditMode">
            <el-icon><Edit /></el-icon>
            <span>发布</span>
          </el-menu-item>

          <el-menu-item index="3" @click="go('/community/mypost')" v-if="isEditMode">
            <el-icon><Bell /></el-icon>
            <span>我的分享</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <!-- 右侧内容 -->
      <main class="content">
        <!-- 顶部操作栏 -->
        <div class="publish-header">
          <el-button type="text" class="back-btn" @click="handleCancel">
            <el-icon><ArrowLeft /></el-icon>
            <span>返回</span>
          </el-button>
          
          <h2 class="page-title">{{ isEditMode ? '编辑文章' : '发布新文章' }}</h2>
        </div>

        <!-- 登录提示 -->
        <div v-if="!userStore.userId" class="login-hint">
          <div class="hint-content">
            <el-icon><WarningFilled /></el-icon>
            <span>马上登录即可发布内容</span>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="isEditMode && loadingPost" class="loading-overlay">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载文章中...</span>
        </div>

        <!-- 发布/编辑表单 -->
        <div class="publish-form" :class="{ 'edit-mode': isEditMode }">
          <h3 class="form-title">{{ isEditMode ? '编辑' : '发布' }}</h3>

          <!-- 图片上传 -->
          <div class="form-section">
            <label class="section-label">上传图片</label>
            <div 
              class="upload-area"
              @click="triggerFileInput"
              @dragover.prevent="handleDragOver"
              @drop.prevent="handleDrop"
            >
              <el-icon v-if="!coverUrl" class="upload-icon"><Picture /></el-icon>
              <img v-else :src="coverUrl" class="preview-image" />
              
              <div v-if="!coverUrl" class="upload-text">
                <p>点击上传图片</p>
                <p class="upload-hint">支持JPG、PNG格式，最大10MB</p>
              </div>
              
              <div v-else class="preview-actions">
                <el-button type="primary" size="small" @click.stop="triggerFileInput">
                  更换图片
                </el-button>
                <el-button type="danger" size="small" @click.stop="removeImage">
                  删除
                </el-button>
              </div>
            </div>
            
            <input
              ref="fileInput"
              type="file"
              accept="image/jpeg,image/png"
              @change="handleFileChange"
              style="display: none"
            />
            
            <div v-if="uploading" class="upload-progress">
              <el-progress 
                :percentage="uploadProgress" 
                :stroke-width="4"
                :show-text="false"
              />
              <span class="progress-text">上传中... {{ uploadProgress }}%</span>
            </div>
          </div>

          <!-- 文章标题 -->
          <div class="form-section">
            <label class="section-label">文章标题</label>
            <el-input
              v-model="form.title"
              placeholder="请输入文章标题..."
              class="title-input"
              size="large"
              maxlength="100"
              show-word-limit
            />
          </div>

          <!-- 文章类型 -->
          <div class="form-section">
            <label class="section-label">文章类型</label>
            <div class="type-buttons">
              <el-button
                v-for="type in postTypes"
                :key="type.value"
                :type="form.type === type.value ? 'primary' : ''"
                :class="{ active: form.type === type.value }"
                @click="selectType(type.value)"
                class="type-btn"
              >
                {{ type.label }}
              </el-button>
            </div>
          </div>

          <!-- 文章内容 -->
          <div class="form-section">
            <label class="section-label">文章内容</label>
            <el-input
              v-model="form.content"
              type="textarea"
              :placeholder="isEditMode ? '编辑文章内容...' : '分享你与宠物的故事，或是你想要分享的内容...'"
              class="content-textarea"
              :rows="8"
              resize="none"
              maxlength="2000"
              show-word-limit
            />
          </div>

          <!-- 操作按钮 -->
          <div class="form-actions">
            <el-button 
              type="info" 
              class="cancel-btn"
              @click="handleCancel"
              :loading="submitting"
            >
              取消
            </el-button>
            <el-button 
              type="primary" 
              class="submit-btn"
              @click="handleSubmit"
              :loading="submitting"
              :disabled="!formValid || !userStore.userId"
            >
              <el-icon><Promotion /></el-icon>
              <span>{{ isEditMode ? '更新文章' : '发布文章' }}</span>
            </el-button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, 
  Picture, 
  Promotion, 
  Edit, 
  Bell,
  WarningFilled,
  Loading 
} from '@element-plus/icons-vue'
import NavBar from '@/components/NavBar.vue'
import { uploadFile } from '@/api/file'
import { createPost, getPost, updatePost } from '@/api/post'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()  // 添加 route
const userStore = useUserStore()
const fileInput = ref(null)

// 判断是否是编辑模式
const isEditMode = computed(() => {
  return route.name === 'EditPost' && route.params.id
})

// 文章ID（编辑模式时使用）
const postId = ref(null)

// 响应式数据
const coverUrl = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)
const submitting = ref(false)
const loadingPost = ref(false)  // 文章加载状态

// 表单数据
const form = reactive({
  title: '',
  content: '',
  type: 'SHARE',
  coverUrl: ''
})

// 文章类型选项（与后端枚举对齐）
const postTypes = [
  { value: 'ANNOUNCEMENT', label: '公告' },
  { value: 'POPULAR_SCIENCE', label: '科普' },
  { value: 'HELP', label: '求助' },
  { value: 'SHARE', label: '分享' }
]

// 表单验证
const formValid = computed(() => {
  return form.title.trim() && form.content.trim() && form.type
})

// 加载文章数据（编辑模式）
const loadPostData = async () => {
  if (!isEditMode.value || !route.params.id) return
  
  loadingPost.value = true
  try {
    const data = await getPost(route.params.id)
    postId.value = data.id
    
    // 回填表单数据
    form.title = data.title || ''
    form.content = data.content || ''
    form.type = data.type || 'SHARE'
    form.coverUrl = data.coverUrl || ''
    
    // 如果有封面图，设置预览
    if (data.coverUrl) {
      coverUrl.value = data.coverUrl
    }
    
  } catch (error) {
    console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败')
    router.push('/sharing')
  } finally {
    loadingPost.value = false
  }
}

// 左侧导航
const go = (path) => {
  router.push(path)
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png']
  if (!validTypes.includes(file.type)) {
    ElMessage.error('只支持 JPG 和 PNG 格式的图片')
    return
  }
  
  // 验证文件大小 (10MB = 10 * 1024 * 1024)
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 10MB')
    return
  }
  
  await uploadImage(file)
}

// 拖拽上传处理
const handleDragOver = (event) => {
  event.dataTransfer.dropEffect = 'copy'
}

const handleDrop = async (event) => {
  const file = event.dataTransfer.files[0]
  if (file && file.type.startsWith('image/')) {
    await uploadImage(file)
  }
}

// 上传图片
const uploadImage = async (file) => {
  try {
    uploading.value = true
    uploadProgress.value = 0
    
    // 创建文件读取器预览
    const reader = new FileReader()
    reader.onload = (e) => {
      coverUrl.value = e.target.result
    }
    reader.readAsDataURL(file)
    
    // 模拟上传进度
    const interval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 100)
    
    // 调用上传接口
    const response = await uploadFile(file)
    
    // 上传完成
    clearInterval(interval)
    uploadProgress.value = 100
    
    // 注意：根据你的接口返回格式调整
    form.coverUrl = response.data  // 假设返回 { data: "url" }
    
    // 等待一下再隐藏进度条
    setTimeout(() => {
      uploading.value = false
      uploadProgress.value = 0
    }, 500)
    
    ElMessage.success('图片上传成功')
    
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('图片上传失败，请重试')
    coverUrl.value = ''
    uploading.value = false
    uploadProgress.value = 0
  }
}

// 删除图片
const removeImage = () => {
  coverUrl.value = ''
  form.coverUrl = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 选择文章类型
const selectType = (type) => {
  form.type = type
}

// 取消操作
const handleCancel = () => {
  if (form.title || form.content || coverUrl.value) {
    ElMessageBox.confirm('确定要取消吗？未保存的内容将会丢失', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      goBack()
    }).catch(() => {})
  } else {
    goBack()
  }
}

// 返回上一页
const goBack = () => {
  if (isEditMode.value && postId.value) {
    // 编辑模式返回文章详情
    router.push(`/community/${postId.value}`)
  } else {
    // 发布模式返回文章列表
    router.push('/sharing')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userStore.userId) {
    ElMessage.warning('请先登录后再操作')
    router.push('/login')
    return
  }
  
  if (!formValid.value) {
    ElMessage.warning('请填写完整的文章信息')
    return
  }
  
  try {
    submitting.value = true
    
    // 构建请求数据
    const postData = {
      title: form.title.trim(),
      content: form.content.trim(),
      type: form.type,
      coverUrl: form.coverUrl
    }
    
    if (isEditMode.value) {
      // 编辑模式：调用更新接口
      await updatePost(postId.value, postData)
      ElMessage.success('文章更新成功！')
      
      // 跳转到文章详情页
      setTimeout(() => {
        router.push(`/community/${postId.value}`)
      }, 1500)
    } else {
      // 发布模式：调用创建接口
      await createPost(postData)
      ElMessage.success('文章发布成功！')
      
      // 延迟跳转
      setTimeout(() => {
        router.push('/sharing')
      }, 1500)
    }
    
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(isEditMode.value ? '更新失败' : '发布失败')
  } finally {
    submitting.value = false
  }
}

// 页面加载
onMounted(() => {
  if (!userStore.userId) {
    ElMessage.info('请先登录后再操作')
  }
  
  // 如果是编辑模式，加载文章数据
  if (isEditMode.value) {
    loadPostData()
  }
})

// 监听路由变化
watch(
  () => route.params.id,
  (newId) => {
    if (newId && isEditMode.value) {
      loadPostData()
    }
  }
)
</script>

<style scoped>
/* 从 sharing 组件提取的布局样式 */
.community-page {
  display: flex;
  background: #f6f3fb;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 240px;
  padding: 20px;
  background: linear-gradient(180deg, #7e57c2, #9575cd);
  border-radius: 0 24px 24px 0;
  color: #fff;
  flex-shrink: 0;
  min-height: calc(100vh - 60px);
  position: relative;
  z-index: 10;
}

.sidebar-header {
  margin-bottom: 30px;
}

.sidebar-header .title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.sidebar-header .subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.sidebar .el-menu {
  border-right: none;
}

.sidebar .el-menu-item {
  height: 56px;
  line-height: 56px;
  font-size: 16px;
  margin: 4px 0;
  border-radius: 12px;
  transition: all 0.3s;
}

.sidebar .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.sidebar .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.15) !important;
  font-weight: 600;
}

.sidebar .el-icon {
  font-size: 20px;
  margin-right: 12px;
}

.content {
  flex: 1;
  margin: 20px;
  padding: 24px;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 2px 12px rgba(126, 87, 194, 0.1);
  overflow: auto;
  min-height: calc(100vh - 100px);
  position: relative;
}

/* 发布头部 */
.publish-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f3e5f5;
  flex-wrap: wrap;
  gap: 20px;
}

.back-btn {
  font-size: 16px;
  color: #7e57c2;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.back-btn:hover {
  color: #5e35b1;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #5e35b1;
  margin: 0;
  padding: 0;
  border: none;
  flex: 1;
  text-align: center;
}

/* 登录提示 */
.login-hint {
  background: linear-gradient(135deg, #fff8e1, #ffecb3);
  border: 1px solid #ffd54f;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 30px;
}

.hint-content {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #f57c00;
  font-weight: 500;
}

.hint-content .el-icon {
  font-size: 20px;
}

/* 加载状态 */
.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
  border-radius: 24px;
  gap: 12px;
  font-size: 16px;
  color: #7e57c2;
  font-weight: 500;
}

.loading-overlay .el-icon {
  font-size: 32px;
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 发布/编辑表单 */
.publish-form {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
}

/* 编辑模式特殊样式 */
.edit-mode .submit-btn {
  background: linear-gradient(135deg, #ff9800, #ffb74d);
}

.edit-mode .submit-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #f57c00, #ff9800);
  box-shadow: 0 6px 16px rgba(245, 124, 0, 0.3);
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: #5e35b1;
  margin-bottom: 30px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f3e5f5;
}

.form-section {
  margin-bottom: 30px;
}

.section-label {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #5e35b1;
  margin-bottom: 12px;
}

/* 上传区域 */
.upload-area {
  width: 100%;
  height: 300px;
  border: 2px dashed #d1c4e9;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f9f5ff;
  overflow: hidden;
  position: relative;
}

.upload-area:hover {
  border-color: #7e57c2;
  background: #f3e5f5;
}

.upload-icon {
  font-size: 64px;
  color: #b39ddb;
  margin-bottom: 16px;
}

.upload-text {
  text-align: center;
  color: #7e57c2;
}

.upload-text p {
  margin: 0;
}

.upload-text p:first-child {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 14px;
  color: #9575cd;
  opacity: 0.8;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 12px;
}

.preview-actions {
  position: absolute;
  bottom: 20px;
  display: flex;
  gap: 12px;
  opacity: 0;
  transition: opacity 0.3s;
}

.upload-area:hover .preview-actions {
  opacity: 1;
}

.upload-progress {
  margin-top: 12px;
  text-align: center;
}

.progress-text {
  display: block;
  margin-top: 8px;
  font-size: 14px;
  color: #7e35b1;
}

/* 标题输入框 */
.title-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid #e1d5f7;
  padding: 12px 16px;
  font-size: 16px;
  height: 48px;
}

.title-input :deep(.el-input__wrapper.is-focus) {
  border-color: #7e57c2;
  box-shadow: 0 0 0 3px rgba(126, 87, 194, 0.1);
}

/* 类型按钮 */
.type-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.type-btn {
  border-radius: 20px;
  padding: 10px 24px;
  font-weight: 500;
  transition: all 0.3s;
  min-width: 100px;
  border: 2px solid #e1d5f7;
  background: white;
  color: #7e57c2;
}

.type-btn.active {
  background: linear-gradient(135deg, #7e57c2, #9575cd);
  border-color: #7e57c2;
  color: white;
  box-shadow: 0 4px 12px rgba(126, 87, 194, 0.2);
}

.type-btn:hover:not(.active) {
  background: #f3e5f5;
  border-color: #d1c4e9;
  transform: translateY(-1px);
}

/* 内容文本框 */
.content-textarea :deep(.el-textarea__inner) {
  border: 2px solid #e1d5f7;
  border-radius: 12px;
  padding: 16px;
  font-size: 16px;
  line-height: 1.6;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
  min-height: 200px;
}

.content-textarea :deep(.el-textarea__inner:focus) {
  border-color: #7e57c2;
  box-shadow: 0 0 0 3px rgba(126, 87, 194, 0.1);
}

.content-textarea :deep(.el-textarea__inner::placeholder) {
  color: #b39ddb;
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid #f3e5f5;
}

.cancel-btn {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 10px;
  border: 2px solid #dcdfe6;
  background: white;
  color: #606266;
  min-width: 100px;
}

.cancel-btn:hover {
  border-color: #c0c4cc;
  background: #f5f7fa;
}

.submit-btn {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 10px;
  background: linear-gradient(135deg, #7e57c2, #9575cd);
  border: none;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(126, 87, 194, 0.2);
  transition: all 0.3s;
  min-width: 140px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(126, 87, 194, 0.3);
  background: linear-gradient(135deg, #6a1b9a, #7e57c2);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .community-page {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    border-radius: 0 0 24px 24px;
    min-height: auto;
    margin-bottom: 0;
  }
  
  .content {
    margin: 20px;
    margin-top: 0;
    border-radius: 24px;
  }
  
  .publish-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .page-title {
    text-align: left;
  }
}

@media (max-width: 768px) {
  .content {
    padding: 20px;
    margin: 12px;
  }
  
  .type-buttons {
    flex-direction: column;
  }
  
  .type-btn {
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .cancel-btn,
  .submit-btn {
    width: 100%;
  }
  
  .upload-area {
    height: 200px;
  }
  
  .publish-header {
    flex-direction: row;
    align-items: center;
  }
  
  .back-btn {
    font-size: 14px;
  }
  
  .page-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .content {
    padding: 16px;
    margin: 8px;
  }
  
  .upload-area {
    height: 180px;
  }
  
  .page-title {
    font-size: 18px;
  }
}
</style>