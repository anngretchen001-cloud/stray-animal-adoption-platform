<template>
  <header>
    <nav-bar />
  </header>

  <div class="animals-page">
    <h1 class="title">{{ pageTitle }}</h1>

    <!-- 搜索和排序（统一容器） -->
    <div class="filters">
      <div class="controls">
        <!-- 排序下拉 -->
        <div class="sort-wrapper">
          <el-dropdown @command="handleSortCommand" trigger="hover">
            <div class="sort-box">
              <span>{{ ageOrderLabel }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="sort-popper">
                <el-dropdown-item command="">默认排序</el-dropdown-item>
                <el-dropdown-item command="asc">年龄升序</el-dropdown-item>
                <el-dropdown-item command="desc">年龄降序</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <!-- 中间：搜索框 + 右侧按钮（作为一组居中） -->
        <div class="search-center">
          <div class="search-wrapper">
            <el-input
              placeholder="Name or Breed"
              v-model="searchQuery"
              class="search-input"
              @keyup.enter="handleSearch"
              clearable
            />
            <el-button type="primary" class="search-btn" @click="handleSearch">Search</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 动物列表 -->
    <div v-if="animals.length > 0" class="animal-list">
      <div 
        v-for="item in animals" 
        :key="item.id" 
        class="animal-card"
        @click="goDetail(item.id)"
      >
        <!-- 修复：使用正确的图片字段 -->
        <img 
          :src="getAnimalImage(item)" 
          :alt="item.name"
          class="animal-photo"
          @error="handleImageError"
        />
        
        <!-- 状态标签 -->
        <div v-if="item.status" class="animal-status">
          <el-tag 
            :type="getStatusType(item.status)" 
            size="small"
          >
            {{ getStatusText(item.status) }}
          </el-tag>
        </div>
        
        <!-- 性别标签 -->
        <div v-if="item.gender" class="animal-gender">
          <el-tag 
            :type="item.gender === 'male' ? 'primary' : 'danger'" 
            size="small"
          >
            {{ item.gender === 'male' ? '公' : '母' }}
          </el-tag>
        </div>
        
        <div class="animal-name" v-html="highlight(item.name)"></div>
        <div class="animal-breed" v-html="highlight(item.breed)"></div>
        <div class="animal-age">{{ item.age }} 岁</div>
        
        <!-- 类型 -->
        <div v-if="item.type" class="animal-type">
          <el-icon><Collection /></el-icon>
          {{ getTypeText(item.type) }}
        </div>
      </div>
    </div>

    <!-- 空列表提示 -->
    <div v-else class="empty-list">
      <el-icon :size="60"><Search /></el-icon>
      <h3>没有找到匹配的动物~</h3>
      <p v-if="searchQuery">尝试其他搜索关键词</p>
    </div>

    <!-- 分页 -->
    <el-pagination
      class="pagination"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      @current-change="handlePageChange"
      v-if="total > 0"
    />
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
import { ref, onMounted, watch, computed } from "vue"
import { useRoute, useRouter } from "vue-router"
import { listAnimals } from "@/api/animal"
import { ArrowDown, Collection, Search } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const pageNum = ref(1)
const pageSize = ref(9)
const total = ref(0)
const animals = ref([])
const loading = ref(false)

// 搜索和排序状态
const searchQuery = ref("")
const ageOrder = ref("")  // "asc" 或 "desc"

// 排序显示文本
const ageOrderLabel = computed(() => {
  if (ageOrder.value === 'asc') return '年龄升序'
  if (ageOrder.value === 'desc') return '年龄降序'
  return '默认排序'
})

function handleSortCommand(command) {
  ageOrder.value = command
  handleSearch()
}

// 页面标题
const pageTitle = computed(() => {
  const type = route.query.type
  if (type === "dog") return "可领养的狗狗"
  if (type === "cat") return "可领养的猫咪"
  return "动物领养中心"
})

// 加载动物列表
async function loadAnimals(resetPage = false) {
  loading.value = true
  const type = route.query.type || null
  if (resetPage) pageNum.value = 1

  try {
    const res = await listAnimals({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      type,
      status: "available",  // 改为小写，匹配后端
      search: searchQuery.value,
      sortByAge: ageOrder.value
    })

    console.log('动物列表API响应:', res)  // 调试用
    
    if (res.data && res.data.data) {
      const records = res.data.data.records || []
      
      // 修复：字段映射转换
      animals.value = records.map(item => ({
        id: item.id,
        name: item.name,
        breed: item.breed,
        age: item.age,
        // 关键修复：从imageUrl映射到photo
        photo: item.imageUrl || '/default-animal.jpg',
        // 其他字段
        gender: item.gender,
        type: item.type,
        status: item.status,
        description: item.description,
        organizationId: item.organizationId
      }))
      
      total.value = res.data.data.total || 0
    } else {
      animals.value = []
      total.value = 0
    }
  } catch (err) {
    console.error("加载动物失败：", err)
    animals.value = []
    ElMessage.error('加载动物列表失败')
  } finally {
    loading.value = false
  }
}

function handlePageChange(p) {
  pageNum.value = p
  loadAnimals()
}

function goDetail(id) {
  router.push(`/animal/${id}`)
}

// 点击搜索或排序
function handleSearch() {
  loadAnimals(true)
}

// 高亮匹配关键字
function highlight(text) {
  if (!searchQuery.value || !text) return text
  const reg = new RegExp(`(${searchQuery.value})`, 'gi')
  return text.replace(reg, '<span class="highlight">$1</span>')
}

// 获取动物图片
function getAnimalImage(item) {
  return item.photo || '/default-animal.jpg'
}

// 图片加载失败处理
function handleImageError(e) {
  console.log('图片加载失败，使用默认图片')
  e.target.src = '/default-animal.jpg'
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
    'pending': '待审核',
    'PENDING': '待审核'
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

onMounted(() => loadAnimals())
watch(() => route.query.type, () => loadAnimals(true))
</script>

<style scoped>
/* 固定导航栏 */
header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.animals-page {
  padding: 6rem 2rem 2rem 2rem; /* 顶部留空间 */
  background: #fdf6ff;
  min-height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
}

/* 标题 */
.title {
  font-size: 2.2rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  color: #7c3aed;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(124, 58, 237, 0.1);
}

/* 搜索和排序栏 */
.filters {
  margin-bottom: 2rem;
  background: white;
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(124, 58, 237, 0.1);
}

.controls {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  width: 100%;
}

.sort-wrapper {
  flex: 0 0 auto;
}
.sort-box {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: #fff;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.95rem;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
  transition: all 0.3s ease;
  min-width: 120px;
  justify-content: center;
}
.sort-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 92, 246, 0.4);
}

/* popper 菜单样式 */
.sort-popper .el-dropdown-menu__item {
  padding: 10px 20px;
  color: #5b21b6;
  font-size: 0.9rem;
}
.sort-popper .el-dropdown-menu {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(99,102,241,0.15);
  border: 1px solid #e2e8f0;
}

.search-center {
  flex: 1 1 auto;
  display: flex;
  justify-content: center;
}
.search-wrapper {
  display: flex;
  align-items: center;
  gap: 0;
  width: 100%;
  max-width: 600px;
}

.search-input {
  flex: 1;
  border-radius: 12px 0 0 12px;
  background-color: #f8fafc;
  padding-left: 1.2rem;
  height: 50px;
  border: 2px solid #e2e8f0;
  border-right: none;
  transition: all 0.3s ease;
}
.search-input:deep(.el-input__wrapper) {
  box-shadow: none;
  background: transparent;
}
.search-input:deep(.el-input__inner) {
  border-radius: 0;
  height: 46px;
}
.search-input:hover {
  border-color: #c7d2fe;
}

.search-btn {
  border-radius: 0 12px 12px 0;
  background: linear-gradient(135deg, #8b5cf6, #7c3aed) !important;
  color: #fff;
  padding: 0 2rem;
  font-weight: 600;
  height: 50px;
  border: 2px solid #8b5cf6;
  border-left: none;
  transition: all 0.3s ease;
}
.search-btn:hover {
  background: linear-gradient(135deg, #7c3aed, #6d28d9) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

/* 动物列表 */
.animal-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.8rem;
  margin-bottom: 2rem;
}

/* 动物卡片 */
.animal-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #f1f5f9;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: center;
  position: relative;
  padding-bottom: 1.2rem;
}

.animal-card:hover {
  border-color: #8b5cf6;
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 30px rgba(139, 92, 246, 0.2);
}

.animal-photo {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-bottom: 2px solid #f1f5f9;
  background: linear-gradient(135deg, #f3e8ff, #ede9fe);
  transition: transform 0.5s ease;
}

.animal-card:hover .animal-photo {
  transform: scale(1.05);
}

/* 状态标签 */
.animal-status {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 2;
}

.animal-gender {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 2;
}

.animal-name {
  font-size: 1.2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 1rem 1rem 0.5rem;
  line-height: 1.3;
  min-height: 1.8em;
  padding: 0 0.5rem;
}

.animal-breed {
  font-size: 0.95rem;
  color: #64748b;
  margin: 0 1rem 0.5rem;
  line-height: 1.3;
  min-height: 1.2em;
  padding: 0 0.5rem;
}

.animal-age {
  font-size: 0.9rem;
  color: #8b5cf6;
  margin: 0 1rem 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-weight: 600;
}

.animal-type {
  font-size: 0.85rem;
  color: #7c3aed;
  margin: 0.5rem 1rem 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 500;
  background: #f5f3ff;
  padding: 6px 12px;
  border-radius: 20px;
  display: inline-flex;
  margin-top: 0.5rem;
}

/* 高亮文字 */
.highlight {
  background-color: #fef3c7;
  border-radius: 4px;
  padding: 0 4px;
  color: #d97706;
  font-weight: 600;
}

/* 空列表提示 */
.empty-list {
  text-align: center;
  padding: 4rem 2rem;
  color: #94a3b8;
  background: white;
  border-radius: 16px;
  margin: 2rem 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 2px dashed #e2e8f0;
}

.empty-list h3 {
  margin: 1.5rem 0 0.5rem 0;
  color: #475569;
  font-size: 1.3rem;
  font-weight: 600;
}

.empty-list p {
  color: #94a3b8;
  font-size: 0.95rem;
  max-width: 400px;
  margin: 0 auto;
  line-height: 1.5;
}

.empty-list .el-icon {
  color: #cbd5e1;
}

/* 分页 */
.pagination {
  margin-top: 2.5rem;
  display: flex;
  justify-content: center;
}

.pagination:deep(.el-pagination.is-background .btn-prev),
.pagination:deep(.el-pagination.is-background .btn-next),
.pagination:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 10px;
  margin: 0 4px;
  min-width: 40px;
  height: 40px;
  line-height: 40px;
}

.pagination:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
  font-weight: 600;
}

/* 加载中 */
.loading {
  text-align: center;
  padding: 3rem;
  color: #8b5cf6;
  font-size: 1.1rem;
}
</style>