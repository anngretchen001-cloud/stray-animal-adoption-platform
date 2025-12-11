<template>
  <NavBar/>
  <div class="org-back-layout">
    <!-- 顶部导航 -->
    <div class="top-bar">组织后台管理</div>

    <div class="main-area">
      <!-- 左侧菜单 -->
      <aside class="sidebar">
        <div
          :class="['menu-item', currentTab === 'animals' ? 'active' : '']"
          @click="switchTab('animals')"
        >
          动物管理
        </div>
        <div
          :class="['menu-item', currentTab === 'applications' ? 'active' : '']"
          @click="switchTab('applications')"
        >
          申请管理
        </div>
      </aside>

      <!-- 右侧内容 -->
      <section class="content">
        <!-- 动物管理 -->
        <div v-if="currentTab === 'animals'" class="card">
          <div class="card-header">
            <h2>动物管理</h2>
            <el-button type="primary" size="small" @click="addNewAnimal">添加动物</el-button>
          </div>

          <el-table :data="animals" border style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column label="图片" width="120">
              <template #default="{ row }">
                <img :src="row.imageUrl" alt="" class="animal-img" />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="type" label="种类" width="80" />
            <el-table-column prop="breed" label="品种" />
            <el-table-column prop="age" label="年龄" width="60" />
            <el-table-column prop="gender" label="性别" width="80" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="editAnimal(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteAnimalRow(row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 申请管理 - 修改这部分 -->
        <div v-if="currentTab === 'applications'" class="card">
          <div class="card-header">
            <h2>领养申请</h2>
            <!-- 状态筛选 -->
            <el-select 
              v-model="filterStatus" 
              placeholder="筛选状态" 
              clearable
              style="width: 120px;"
              @change="loadApplications"
            >
              <el-option label="全部" value="" />
              <el-option label="待处理" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </div>
          
          <el-table :data="applications" border style="width: 100%" v-loading="loadingApplications">
            <el-table-column prop="id" label="ID" width="60" />
            
            <!-- 动物信息列 -->
            <el-table-column label="动物信息" width="200">
              <template #default="{ row }">
                <div class="animal-info">
                  <img 
                    v-if="row.animalImageUrl" 
                    :src="row.animalImageUrl" 
                    alt="动物图片"
                    class="animal-thumb"
                    @error="handleImageError"
                  />
                  <div class="animal-details">
                    <div class="animal-name">
                      {{ row.animalName || '未知动物' }}
                    </div>
                    <div class="animal-subinfo">
                      <div v-if="row.breed">{{ row.breed }}</div>
                      <div v-if="row.age">{{ row.age }}岁</div>
                      <div v-if="row.gender">
                        {{ row.gender === 'male' ? '公' : row.gender === 'female' ? '母' : row.gender }}
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <!-- 申请人信息列 -->
            <el-table-column label="申请人" width="180">
              <template #default="{ row }">
                <div class="applicant-info">
                  <div class="applicant-name">
                    {{ row.applicantName || '用户' + row.userId }}
                  </div>
                  <div class="applicant-contact">
                    <div v-if="row.applicantEmail" class="contact-item">
                      <el-icon><Message /></el-icon>
                      <span>{{ row.applicantEmail }}</span>
                    </div>
                    <div v-if="row.applicantPhone" class="contact-item">
                      <el-icon><Phone /></el-icon>
                      <span>{{ row.applicantPhone }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <!-- 申请理由 -->
            <el-table-column prop="reason" label="申请理由" width="200">
              <template #default="{ row }">
                <div class="reason-content" :title="row.reason">
                  {{ row.reason || '无' }}
                </div>
              </template>
            </el-table-column>
            
            <!-- 申请时间 -->
            <el-table-column label="申请时间" width="120">
              <template #default="{ row }">
                <div style="font-size: 12px;">
                  {{ formatDate(row.createdAt) }}
                </div>
              </template>
            </el-table-column>
            
            <!-- 状态列 -->
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag 
                  :type="getStatusTagType(row.status)" 
                  size="small"
                >
                  {{ formatStatus(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <!-- 操作列 -->
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button 
                    v-if="row.status === 'PENDING' || row.status === 'pending'" 
                    type="success" 
                    size="small" 
                    @click="approve(row.id)"
                    :loading="row.approving"
                  >
                    通过
                  </el-button>
                  <el-button 
                    v-if="row.status === 'PENDING' || row.status === 'pending'" 
                    type="danger" 
                    size="small" 
                    @click="reject(row.id)"
                    :loading="row.rejecting"
                  >
                    拒绝
                  </el-button>
                  
                  <!-- 已处理的申请可以查看详情 -->
                  <el-button 
                    v-if="row.status !== 'PENDING' && row.status !== 'pending'" 
                    type="info" 
                    size="small" 
                    @click="viewApplicationDetail(row)"
                  >
                    详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </section>
    </div>

    <!-- 只修复弹窗部分，保持API调用不变 -->
    <el-dialog
      :title="editingAnimal ? '编辑动物' : '添加动物'"
      v-model="showAddAnimal"  
      width="500px"
      :destroy-on-close="true"
    >
      <el-form :model="animalForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="animalForm.name" /></el-form-item>
        <el-form-item label="种类">
          <el-select v-model="animalForm.type" placeholder="请选择">
            <el-option label="猫" value="cat" />
            <el-option label="狗" value="dog" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种"><el-input v-model="animalForm.breed" /></el-form-item>
        <el-form-item label="年龄"><el-input type="number" v-model.number="animalForm.age" /></el-form-item>
        <el-form-item label="性别">
          <el-select v-model="animalForm.gender" placeholder="请选择">
            <el-option label="母" value="female" />
            <el-option label="公" value="male" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input type="textarea" v-model="animalForm.description" /></el-form-item>
        
        <!-- 修改这里：从URL输入改为文件上传 -->
        <el-form-item label="图片">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :http-request="handleUpload"
            :before-upload="beforeUpload"
            accept=".jpg,.jpeg,.png,.gif,.webp"
          >
            <div v-if="animalForm.imageUrl" class="image-preview">
              <img :src="animalForm.imageUrl" alt="宠物图片" class="uploaded-image" />
              <div class="image-hover-text">点击重新上传</div>
            </div>
            <div v-else class="upload-placeholder">
              <el-icon :size="40" class="upload-icon"><Plus /></el-icon>
              <div class="upload-text">点击上传图片</div>
              <div class="upload-hint">支持 JPG、PNG、GIF 格式</div>
            </div>
          </el-upload>
          
          <!-- 保留URL输入作为备用 -->
          <div style="margin-top: 10px; font-size: 12px; color: #666;">
            或输入图片URL：
            <el-input 
              v-model="animalForm.imageUrl" 
              placeholder="输入图片URL" 
              size="small"
              style="margin-top: 5px;"
            />
          </div>
        </el-form-item>
      </el-form>

      <div class="dialog-footer" style="text-align: right;">
        <el-button @click="cancelEdit">取消</el-button>
        <el-button type="primary" @click="submitAnimal">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Message, Phone } from '@element-plus/icons-vue'
import { listOrgAnimals, addAnimal, updateAnimal, deleteAnimal } from '@/api/animal'
import { getOrgApplications, approveApplication, rejectApplication } from '@/api/application'
import { uploadFile } from '@/api/file'  // 这里需要根据您的实际路径调整
import NavBar from '../components/NavBar.vue'

const currentTab = ref('animals')
const animals = ref([])
const applications = ref([])
const loading = ref(false)
const loadingApplications = ref(false)
const filterStatus = ref('')  // 状态筛选

// 弹窗相关
const showAddAnimal = ref(false)
const editingAnimal = ref(null)
const animalForm = ref({
  name: '',
  type: '',
  breed: '',
  age: null,
  gender: '',
  description: '',
  imageUrl: ''
})

// 图片上传前验证
const beforeUpload = (file) => {
  const isImage = /\.(jpg|jpeg|png|gif|webp)$/i.test(file.name)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB！')
    return false
  }
  return true
}

// 自定义上传处理
const handleUpload = async ({ file, onSuccess, onError }) => {
  if (!beforeUpload(file)) {
    onError('文件验证失败')
    return
  }

  try {
    // 使用您的uploadFile API
    const res = await uploadFile(file)
    
    // 根据您的API响应结构调整
    const imageUrl = res.data?.data || res.data?.url || res.url
    if (imageUrl) {
      animalForm.value.imageUrl = imageUrl
      onSuccess(res)
      ElMessage.success('图片上传成功')
    } else {
      throw new Error('未获取到图片URL')
    }
  } catch (err) {
    console.error('图片上传失败:', err)
    onError(err)
    const errorMsg = err.response?.data?.message || err.message || '上传失败'
    ElMessage.error(`上传失败: ${errorMsg}`)
  }
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '-'
  try {
    const date = new Date(dateStr)
    return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
  } catch (e) {
    return '-'
  }
}

// 格式化状态
function formatStatus(status) {
  const upperStatus = status ? status.toUpperCase() : ''
  const map = {
    'PENDING': '待处理',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝',
    'CANCELLED': '已取消',
    'pending': '待处理',
    'approved': '已通过',
    'rejected': '已拒绝',
    'cancelled': '已取消'
  }
  return map[status] || upperStatus
}

// 状态标签类型
function getStatusTagType(status) {
  const upperStatus = status ? status.toUpperCase() : ''
  const map = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info'
  }
  return map[upperStatus] || 'info'
}

// 图片加载失败处理
function handleImageError(e) {
  e.target.src = '/default-animal.jpg'
}

// 保持原有的API调用逻辑
function switchTab(tab) {
  currentTab.value = tab
  if (tab === 'animals') loadAnimals()
  if (tab === 'applications') loadApplications()
}

async function loadAnimals() {
  try {
    loading.value = true
    const res = await listOrgAnimals()
    console.log('动物列表响应:', res)
    animals.value = res.data.data.records || []
  } catch (err) {
    console.error('加载失败:', err)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 修改申请加载函数
async function loadApplications() {
  try {
    loadingApplications.value = true
    
    // 调用API，传入筛选参数
    const res = await getOrgApplications({
      status: filterStatus.value
    })
    
    console.log('申请列表响应:', res)
    
    if (res.data && res.data.code === 0) {
      // 使用新的DTO结构
      applications.value = (res.data.data.records || []).map(app => ({
        id: app.id,
        animalId: app.animalId,
        userId: app.userId,
        animalName: app.animalName,
        breed: app.breed,
        age: app.age,
        gender: app.gender,
        animalImageUrl: app.animalImageUrl,
        animalType: app.animalType,
        applicantName: app.applicantName,
        applicantEmail: app.applicantEmail,
        applicantPhone: app.applicantPhone,
        status: app.status,
        reason: app.reason,
        createdAt: app.createdAt,
        updatedAt: app.updatedAt,
        // 添加loading状态
        approving: false,
        rejecting: false
      }))
    } else {
      applications.value = []
    }
    
  } catch (err) {
    console.error('加载失败:', err)
    ElMessage.error('加载失败')
  } finally {
    loadingApplications.value = false
  }
}

// 弹窗相关函数
function editAnimal(row) {
  console.log('编辑按钮点击')
  editingAnimal.value = row
  animalForm.value = { ...row }
  showAddAnimal.value = true
}

function addNewAnimal() {
  console.log('添加按钮点击')
  editingAnimal.value = null
  animalForm.value = {
    name: '',
    type: '',
    breed: '',
    age: null,
    gender: '',
    description: '',
    imageUrl: ''
  }
  showAddAnimal.value = true
}

function cancelEdit() {
  editingAnimal.value = null
  animalForm.value = {
    name: '',
    type: '',
    breed: '',
    age: null,
    gender: '',
    description: '',
    imageUrl: ''
  }
  showAddAnimal.value = false
}

async function submitAnimal() {
  try {
    if (editingAnimal.value) {
      await updateAnimal(editingAnimal.value.id, animalForm.value)
      ElMessage.success('更新成功')
    } else {
      await addAnimal(animalForm.value)
      ElMessage.success('添加成功')
    }
    cancelEdit()
    loadAnimals()
  } catch (err) {
    console.error(err)
    ElMessage.error('操作失败')
  }
}

async function deleteAnimalRow(id) {
  try {
    await ElMessageBox.confirm(
      '确定要删除这只动物吗？此操作不可撤销。',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    await deleteAnimal(id)
    ElMessage.success('删除成功')
    loadAnimals()
  } catch (err) {
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('删除失败')
    }
  }
}

// 修改通过申请函数
async function approve(id) {
  try {
    const application = applications.value.find(app => app.id === id)
    if (!application) return
    
    await ElMessageBox.confirm(
      `确定要通过【${application.animalName}】的领养申请吗？`,
      '通过确认',
      {
        confirmButtonText: '确定通过',
        cancelButtonText: '取消',
        type: 'success',
      }
    )
    
    // 设置loading状态
    application.approving = true
    
    await approveApplication(id)
    
    // 更新状态
    application.status = 'approved'
    application.approving = false
    
    ElMessage.success('已通过申请')
  } catch (err) {
    if (err !== 'cancel') {
      console.error('通过申请失败:', err)
      ElMessage.error('操作失败')
      
      // 恢复loading状态
      const application = applications.value.find(app => app.id === id)
      if (application) {
        application.approving = false
      }
    }
  }
}

// 修改拒绝申请函数
async function reject(id) {
  try {
    const application = applications.value.find(app => app.id === id)
    if (!application) return
    
    await ElMessageBox.confirm(
      `确定要拒绝【${application.animalName}】的领养申请吗？`,
      '拒绝确认',
      {
        confirmButtonText: '确定拒绝',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    // 设置loading状态
    application.rejecting = true
    
    await rejectApplication(id)
    
    // 更新状态
    application.status = 'rejected'
    application.rejecting = false
    
    ElMessage.success('已拒绝申请')
  } catch (err) {
    if (err !== 'cancel') {
      console.error('拒绝申请失败:', err)
      ElMessage.error('操作失败')
      
      // 恢复loading状态
      const application = applications.value.find(app => app.id === id)
      if (application) {
        application.rejecting = false
      }
    }
  }
}

// 查看申请详情
function viewApplicationDetail(row) {
  ElMessageBox.alert(`
    <div class="application-detail">
      <h3 style="margin-top: 0; color: #5a2e8a;">申请详情</h3>
      
      <div class="detail-section">
        <h4>动物信息</h4>
        <div class="detail-grid">
          <div><strong>名称：</strong>${row.animalName || '-'}</div>
          <div><strong>品种：</strong>${row.breed || '-'}</div>
          <div><strong>年龄：</strong>${row.age || '-'}岁</div>
          <div><strong>性别：</strong>${row.gender === 'male' ? '公' : row.gender === 'female' ? '母' : row.gender}</div>
        </div>
      </div>
      
      <div class="detail-section">
        <h4>申请人信息</h4>
        <div class="detail-grid">
          <div><strong>姓名：</strong>${row.applicantName || '-'}</div>
          <div><strong>邮箱：</strong>${row.applicantEmail || '-'}</div>
          <div><strong>电话：</strong>${row.applicantPhone || '-'}</div>
        </div>
      </div>
      
      <div class="detail-section">
        <h4>申请信息</h4>
        <div><strong>状态：</strong><span class="status-${row.status ? row.status.toLowerCase() : ''}">${formatStatus(row.status)}</span></div>
        <div><strong>申请时间：</strong>${formatDate(row.createdAt)}</div>
        <div style="margin-top: 8px;"><strong>申请理由：</strong></div>
        <div class="reason-box">${row.reason || '无'}</div>
      </div>
    </div>
  `, '申请详情', {
    dangerouslyUseHTMLString: true,
    showConfirmButton: false,
    showCancelButton: true,
    cancelButtonText: '关闭',
    customClass: 'detail-dialog'
  })
}

onMounted(() => {
  loadAnimals()
})
</script>

<style scoped>
/* 保持您的原有样式不变 */
.org-back-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f4f0fa;
  font-family: 'Segoe UI', sans-serif;
}

.top-bar {
  height: 60px;
  line-height: 60px;
  background: #d8c1ff;
  padding-left: 20px;
  font-weight: bold;
  font-size: 18px;
  color: #5a2e8a;
}

.main-area {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 180px;
  background: #e8d9ff;
  display: flex;
  flex-direction: column;
  padding: 15px;
  border-radius: 0 15px 15px 0;
}

.menu-item {
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 8px;
  cursor: pointer;
  background: #f0e0ff;
  text-align: center;
  color: #5a2e8a;
  font-weight: 500;
  transition: 0.2s;
}

.menu-item:hover {
  background: #d9c1ff;
}

.menu-item.active {
  background: #b891ff;
  color: white;
  font-weight: bold;
}

.content {
  flex: 1;
  padding: 20px;
}

.card {
  background: #f3e8ff;
  border-radius: 15px;
  padding: 15px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.animal-img {
  width: 100px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
}

/* 上传组件样式 */
.avatar-uploader {
  width: 100%;
  cursor: pointer;
  position: relative;
}

.upload-placeholder {
  width: 100%;
  height: 150px;
  border: 2px dashed #dcdfe6;
  border-radius: 6px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
  transition: border-color 0.3s;
  background-color: #fafafa;
}

.upload-placeholder:hover {
  border-color: #8b5cf6;
}

.upload-icon {
  color: #8b5cf6;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #666;
}

.upload-hint {
  font-size: 12px;
  color: #999;
}

.image-preview {
  width: 100%;
  position: relative;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #eee;
}

.uploaded-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  transition: transform 0.3s;
}

.image-preview:hover .uploaded-image {
  transform: scale(1.05);
}

.image-hover-text {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-preview:hover .image-hover-text {
  opacity: 1;
}

/* 新增样式 - 动物信息 */
.animal-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.animal-thumb {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  border: 1px solid #eee;
  background-color: #f5f5f5;
}

.animal-details {
  flex: 1;
}

.animal-name {
  font-weight: 600;
  color: #5a2e8a;
  margin-bottom: 4px;
  font-size: 14px;
}

.animal-subinfo {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

/* 新增样式 - 申请人信息 */
.applicant-info {
  font-size: 13px;
}

.applicant-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.applicant-contact {
  color: #666;
  font-size: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

/* 申请理由样式 */
.reason-content {
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  font-size: 13px;
  color: #666;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>

<style>
/* 详情弹窗样式 */
.detail-dialog .el-message-box {
  width: 500px;
  max-width: 90vw;
}

.application-detail .detail-section {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.application-detail .detail-section h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 15px;
  font-weight: 600;
}

.application-detail .detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  font-size: 14px;
}

.application-detail .reason-box {
  margin-top: 8px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.5;
  color: #666;
}

.application-detail .status-pending {
  color: #e6a23c;
  font-weight: 500;
}

.application-detail .status-approved {
  color: #67c23a;
  font-weight: 500;
}

.application-detail .status-rejected {
  color: #f56c6c;
  font-weight: 500;
}

.application-detail .status-cancelled {
  color: #909399;
  font-weight: 500;
}
</style>