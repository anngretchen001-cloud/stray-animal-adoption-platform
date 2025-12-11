<template>
  <div class="animal-detail-page">
    <el-button class="back-btn" @click="goBack">
      <el-icon><ArrowLeft /></el-icon>
      <span>返回列表</span>
    </el-button>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="loading-icon" :size="60"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <!-- 动物详情卡片 -->
    <el-card v-else-if="animal" class="animal-card" shadow="hover">
      <div class="animal-header">
        <!-- 左边图片 -->
        <div class="image-container">
          <img 
            :src="getAnimalImage(animal)" 
            :alt="animal.name"
            class="animal-photo"
            @error="handleImageError"
          />
          
          <!-- 状态标签 -->
          <div class="status-badge">
            <el-tag 
              :type="getStatusType(animal.status)" 
              size="large"
              effect="dark"
            >
              {{ getStatusText(animal.status) }}
            </el-tag>
          </div>
          
          <!-- 性别标签 -->
          <div class="gender-badge">
            <el-tag 
              :type="animal.gender === 'male' ? 'primary' : 'danger'" 
              size="large"
              effect="dark"
            >
              {{ animal.gender === 'male' ? '公' : '母' }}
            </el-tag>
          </div>
        </div>

        <!-- 右边基本信息 -->
        <div class="animal-info">
          <div class="info-header">
            <h1 class="animal-name">{{ animal.name }}</h1>
            <div class="animal-type">
              <el-tag 
                :type="animal.type === 'dog' ? 'primary' : animal.type === 'cat' ? 'success' : 'info'"
                size="large"
              >
                <el-icon><Collection /></el-icon>
                {{ getTypeText(animal.type) }}
              </el-tag>
            </div>
          </div>
          
          <div class="info-grid">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span class="label">名字</span>
              <span class="value">{{ animal.name }}</span>
            </div>
            
            <div class="info-item">
              <el-icon><Calendar /></el-icon>
              <span class="label">年龄</span>
              <span class="value">{{ animal.age }} 岁</span>
            </div>
            
            <div class="info-item">
              <el-icon><Medal /></el-icon>
              <span class="label">品种</span>
              <span class="value">{{ animal.breed || '未知' }}</span>
            </div>
            
            <div class="info-item">
              <el-icon><Flag /></el-icon>
              <span class="label">性别</span>
              <span class="value">{{ animal.gender === 'male' ? '公' : animal.gender === 'female' ? '母' : animal.gender }}</span>
            </div>
            
            <div v-if="animal.organizationName" class="info-item">
              <el-icon><OfficeBuilding /></el-icon>
              <span class="label">所属组织</span>
              <span class="value">{{ animal.organizationName }}</span>
            </div>
            
            <div v-if="animal.createdAt" class="info-item">
              <el-icon><Clock /></el-icon>
              <span class="label">加入时间</span>
              <span class="value">{{ formatDate(animal.createdAt) }}</span>
            </div>
          </div>
          
          <!-- 申请按钮 -->
          <div class="action-buttons">
            <el-button 
              v-if="animal.status === 'available'" 
              type="primary" 
              class="adopt-btn"
              @click="showAdoptDialog = true"
              :disabled="!animal.status || animal.status !== 'available'"
            >
              <el-icon><Promotion /></el-icon>
              <span>申请领养</span>
            </el-button>
            
            <el-button 
              v-else 
              type="info" 
              disabled
              class="adopt-btn"
            >
              <el-icon><Lock /></el-icon>
              <span>已{{ animal.status === 'adopted' ? '被领养' : '不可领养' }}</span>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 详细描述 -->
      <div class="animal-description">
        <h3 class="section-title">
          <el-icon><Document /></el-icon>
          <span>动物介绍</span>
        </h3>
        <div class="description-content">
          {{ animal.description || '暂无详细介绍' }}
        </div>
      </div>
    </el-card>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-icon :size="80" color="#cbd5e1"><Search /></el-icon>
      <h3>动物信息不存在</h3>
      <p>抱歉，没有找到这只动物的相关信息</p>
      <el-button type="primary" @click="goBack" class="back-home-btn">
        返回列表
      </el-button>
    </div>

    <!-- 申请领养弹窗 -->
    <el-dialog
      v-model="showAdoptDialog"
      title="申请领养"
      width="500px"
      :destroy-on-close="true"
      class="adopt-dialog"
      @close="resetForm"
    >
      <div class="dialog-header">
        <div class="dialog-animal-info">
          <img 
            :src="getAnimalImage(animal)" 
            :alt="animal?.name"
            class="dialog-animal-photo"
            @error="handleImageError"
          />
          <div>
            <h4>{{ animal?.name }}</h4>
            <p>{{ animal?.breed }} • {{ animal?.age }}岁</p>
          </div>
        </div>
      </div>
      
      <el-form 
        :model="applicationForm" 
        :rules="formRules" 
        ref="formRef"
        label-width="80px"
        class="adopt-form"
      >
        <el-form-item label="申请理由" prop="reason">
          <el-input
            type="textarea"
            v-model="applicationForm.reason"
            placeholder="请告诉我们您想领养这只宠物的理由，让我们更好地了解您..."
            :rows="5"
            resize="none"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <div class="dialog-notes">
        <h4><el-icon><InfoFilled /></el-icon> 申请须知</h4>
        <ul>
          <li>请确保您有能力照顾宠物的一生</li>
          <li>领养申请审核通常需要1-3个工作日</li>
          <li>审核通过后，组织会与您联系安排后续事宜</li>
        </ul>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAdoptDialog = false" :disabled="submitting">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitAdoptionForm" 
            :loading="submitting"
            :disabled="!applicationForm.reason?.trim()"
          >
            {{ submitting ? '提交中...' : '提交申请' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, 
  Loading, 
  Collection, 
  User, 
  Calendar, 
  Medal, 
  Flag, 
  OfficeBuilding, 
  Clock, 
  Promotion, 
  Lock, 
  Document, 
  Search, 
  InfoFilled 
} from '@element-plus/icons-vue'
import { getAnimal } from '@/api/animal'
import { submitAdoption } from '@/api/application'

const route = useRoute()
const router = useRouter()

const animal = ref(null)
const loading = ref(true)
const showAdoptDialog = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 申请表单
const applicationForm = reactive({
  reason: ''
})

// 表单验证规则
const formRules = {
  reason: [
    { required: true, message: '请填写申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由至少10个字符', trigger: 'blur' },
    { max: 500, message: '申请理由不能超过500个字符', trigger: 'blur' }
  ]
}

// 加载动物详情
async function loadAnimal() {
  loading.value = true
  const id = route.params.id
  try {
    const res = await getAnimal(id)
    console.log('动物详情响应:', res)  // 调试用
    
    if (res.data && res.data.data) {
      const data = res.data.data
      // 字段映射
      animal.value = {
        id: data.id,
        name: data.name,
        breed: data.breed,
        age: data.age,
        gender: data.gender,
        type: data.type,
        status: data.status,
        description: data.description,
        // 修复：从imageUrl映射
        photo: data.imageUrl,
        // 组织信息
        organizationId: data.organizationId,
        organizationName: data.organizationName,
        createdAt: data.createdAt
      }
    } else {
      animal.value = null
    }
  } catch (err) {
    console.error('加载动物详情失败：', err)
    ElMessage.error('加载动物详情失败')
    animal.value = null
  } finally {
    loading.value = false
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 提交领养申请
async function submitAdoptionForm() {
  if (!animal.value) return
  
  try {
    // 表单验证
    if (!formRef.value) return
    
    const valid = await formRef.value.validate()
    if (!valid) {
      ElMessage.warning('请填写完整的申请信息')
      return
    }
    
    submitting.value = true
    
    await submitAdoption({
      animalId: animal.value.id,
      reason: applicationForm.reason  // 注意字段名
    })
    
    ElMessage.success('领养申请已提交，请等待审核')
    showAdoptDialog.value = false
    resetForm()
    
    // 重新加载动物信息，更新状态
    loadAnimal()
    
  } catch (err) {
    console.error('提交申请失败', err)
    const errorMsg = err.response?.data?.message || err.message || '提交失败，请重试'
    ElMessage.error(errorMsg)
  } finally {
    submitting.value = false
  }
}

// 重置表单
function resetForm() {
  applicationForm.reason = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 获取动物图片
function getAnimalImage(item) {
  if (!item) return '/default-animal.jpg'
  return item.photo || item.imageUrl || '/default-animal.jpg'
}

// 图片加载失败处理
function handleImageError(e) {
  console.log('图片加载失败，使用默认图片')
  e.target.src = '/default-animal.jpg'
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '未知'
  try {
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
  } catch (e) {
    return dateStr
  }
}

// 获取状态标签类型
function getStatusType(status) {
  const statusMap = {
    'available': 'success',
    'AVAILABLE': 'success',
    'adopted': 'info',
    'ADOPTED': 'info',
    'pending': 'warning',
    'PENDING': 'warning'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
function getStatusText(status) {
  const textMap = {
    'available': '可领养',
    'AVAILABLE': '可领养',
    'adopted': '已领养',
    'ADOPTED': '已领养',
    'pending': '审核中',
    'PENDING': '审核中'
  }
  return textMap[status] || status
}

// 获取类型文本
function getTypeText(type) {
  const typeMap = {
    'dog': '狗狗',
    'cat': '猫咪',
    'other': '其他'
  }
  return typeMap[type] || type
}

onMounted(() => {
  loadAnimal()
})
</script>

<style scoped>
.animal-detail-page {
  padding: 6rem 2rem 2rem;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  min-height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
}

/* 返回按钮 */
.back-btn {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-weight: 600;
  font-size: 1rem;
  margin-bottom: 2rem;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 5rem 2rem;
  color: #8b5cf6;
  font-size: 1.1rem;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  margin-bottom: 1rem;
  color: #8b5cf6;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 动物卡片 */
.animal-card {
  border-radius: 20px;
  border: 2px solid #f1f5f9;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  background: white;
}

.animal-header {
  display: flex;
  gap: 3rem;
  padding: 2.5rem;
  flex-wrap: wrap;
  border-bottom: 2px solid #f1f5f9;
}

/* 图片容器 */
.image-container {
  flex: 0 0 350px;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.animal-photo {
  width: 100%;
  height: 350px;
  object-fit: cover;
  background: linear-gradient(135deg, #f3e8ff, #ede9fe);
  transition: transform 0.5s ease;
}

.image-container:hover .animal-photo {
  transform: scale(1.05);
}

.status-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 2;
}

.gender-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 2;
}

/* 信息区域 */
.animal-info {
  flex: 1;
  min-width: 300px;
}

.info-header {
  margin-bottom: 2rem;
}

.animal-name {
  font-size: 2.5rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 1rem;
  line-height: 1.2;
}

.animal-type {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2.5rem;
  background: #f8fafc;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0.8rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.info-item:hover {
  border-color: #c7d2fe;
  box-shadow: 0 2px 8px rgba(199, 210, 254, 0.3);
  transform: translateY(-2px);
}

.info-item .el-icon {
  color: #8b5cf6;
  font-size: 1.2rem;
}

.info-item .label {
  color: #64748b;
  font-weight: 500;
  font-size: 0.9rem;
  min-width: 60px;
}

.info-item .value {
  color: #1e293b;
  font-weight: 600;
  font-size: 1rem;
  flex: 1;
}

/* 操作按钮 */
.action-buttons {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 2px solid #f1f5f9;
}

.adopt-btn {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 1rem 2.5rem;
  font-size: 1.1rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
  min-width: 200px;
}

.adopt-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
}

.adopt-btn:disabled {
  background: #cbd5e1;
  box-shadow: none;
  cursor: not-allowed;
}

/* 描述区域 */
.animal-description {
  padding: 2.5rem;
  background: #f8fafc;
  border-radius: 0 0 20px 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #1e293b;
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e2e8f0;
}

.section-title .el-icon {
  color: #8b5cf6;
}

.description-content {
  color: #475569;
  line-height: 1.8;
  font-size: 1.05rem;
  white-space: pre-line;
  padding: 1rem 0;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 5rem 2rem;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  border: 2px dashed #e2e8f0;
}

.empty-state h3 {
  margin: 1.5rem 0 0.5rem 0;
  color: #475569;
  font-size: 1.5rem;
  font-weight: 600;
}

.empty-state p {
  color: #94a3b8;
  font-size: 1rem;
  max-width: 400px;
  margin: 0 auto 2rem;
  line-height: 1.5;
}

.back-home-btn {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.8rem 2rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-home-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

/* 弹窗样式 */
.adopt-dialog :deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  border: 2px solid #f1f5f9;
}

.adopt-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  padding: 1.5rem 2rem;
  margin: 0;
  color: white;
}

.adopt-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 1.2rem;
}

.dialog-header {
  padding: 1.5rem 0;
  border-bottom: 2px solid #f1f5f9;
  margin-bottom: 1.5rem;
}

.dialog-animal-info {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.dialog-animal-photo {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  object-fit: cover;
  border: 2px solid #e2e8f0;
}

.dialog-animal-info h4 {
  margin: 0 0 4px 0;
  color: #1e293b;
  font-size: 1.1rem;
  font-weight: 600;
}

.dialog-animal-info p {
  margin: 0;
  color: #64748b;
  font-size: 0.9rem;
}

.adopt-form {
  padding: 1rem 0;
}

.adopt-form :deep(.el-textarea__inner) {
  border-radius: 10px;
  border: 2px solid #e2e8f0;
  padding: 1rem;
  font-size: 0.95rem;
  line-height: 1.5;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.adopt-form :deep(.el-textarea__inner:focus) {
  border-color: #8b5cf6;
  box-shadow: 0 0 0 2px rgba(139, 92, 246, 0.1);
  background: white;
}

.dialog-notes {
  margin-top: 1.5rem;
  padding: 1.5rem;
  background: #f0f9ff;
  border-radius: 12px;
  border: 1px solid #bae6fd;
}

.dialog-notes h4 {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #0369a1;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.dialog-notes ul {
  margin: 0;
  padding-left: 1.5rem;
  color: #475569;
  font-size: 0.9rem;
  line-height: 1.6;
}

.dialog-notes li {
  margin-bottom: 0.5rem;
}

.dialog-footer {
  padding: 1.5rem 0 0 0;
  border-top: 2px solid #f1f5f9;
  text-align: right;
}

.dialog-footer .el-button {
  border-radius: 10px;
  padding: 0.8rem 2rem;
  font-weight: 600;
  font-size: 1rem;
  min-width: 100px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  border: none;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

.dialog-footer .el-button--primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #7c3aed, #6d28d9);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .animal-detail-page {
    padding: 5rem 1rem 1rem;
  }
  
  .animal-header {
    flex-direction: column;
    gap: 2rem;
    padding: 1.5rem;
  }
  
  .image-container {
    flex: none;
    width: 100%;
  }
  
  .animal-name {
    font-size: 2rem;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .adopt-btn {
    width: 100%;
  }
}
</style>