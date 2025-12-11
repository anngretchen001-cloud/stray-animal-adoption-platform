<template>
  <div class="petfinder-container">
    <!-- 顶部导航栏 -->
    <header class="main-header">
      <nav class="navbar">
        <div class="nav-left">
          <div class="logo">
            <span class="logo-text">Animal-shelter</span>
          </div>
          <ul class="nav-menu">
            <li class="nav-item">
              <a href="#" class="nav-link">FIND A PET</a>
            </li>
            <li class="nav-item">
              <a href="#" class="nav-link">ALL ABOUT PETS</a>
            </li>
            <li class="nav-item">
              <a href="#" class="nav-link issue-link">
                Experiencing site issues? We're on it- See Updates and Tips.
              </a>
            </li>
          </ul>
        </div>

        <!-- 右上角：头像 + 下拉菜单 -->
        <div class="nav-right">
          <el-dropdown>
            <span class="avatar-wrapper">
              <el-avatar
                :src="userStore.avatar || defaultAvatar"
                size="medium"
              />
            </span>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goProfile">
                  个人信息
                </el-dropdown-item>

                <el-dropdown-item
                  v-if="['ORG', 'ADMIN'].includes(userStore.role)"
                  @click="goBackend"
                >
                  后台管理
                </el-dropdown-item>

                <el-dropdown-item divided @click="logout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </nav>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <div class="search-container">
          <input 
            type="text" 
            placeholder="Search terrier, kitten, etc."
            class="search-input"
            v-model="searchTerm"
          />
          <input 
            type="text" 
            placeholder="China"
            class="location-input"
            v-model="location"
          />
          <button class="search-btn" @click="handleSearch">Search</button>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="main-content">
      <div class="pet-background">
        <div class="background-overlay"></div>
      </div>
      
      <div class="hero-content">
        <h1 class="main-title">
          Find your New Best Friend and Adopt a Pet with Animal-shelter
        </h1>
        <p class="subtitle">
          Browse pets from our network of shelters and rescues all over the country.
        </p>
        
        <div class="action-buttons">
          <button 
            v-for="button in buttons" 
            :key="button.id"
            :class="['action-button', { 'primary': button.primary }]"
            @click="handleButtonClick(button.id)"
          >
            {{ button.text }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

// store / router
const userStore = useUserStore()
const router = useRouter()

const defaultAvatar = '/default-avatar.png'

// 按钮数据
const buttons = ref([
  { id: 'dog', text: 'Dogs', primary: false },
  { id: 'cat', text: 'Cats', primary: false },
  { id: 'other-animal', text: 'Other Animals', primary: false },
  { id: 'shelter', text: 'Shelters & Rescues', primary: true }
])

// 搜索
const searchTerm = ref('')
const location = ref('Hong Kong')

// 跳转逻辑
function goProfile() {
  router.push('/profile')
}

function goBackend() {
  if (userStore.role === 'ORG') router.push('/organization')
  else if (userStore.role === 'ADMIN') router.push('/admin')
}

function logout() {
  userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}

function handleButtonClick(type) {
  router.push({
    path: '/animals',
    query: { type }  // dogs / cats / other
  })
}


function handleSearch() {
  console.log('Searching for:', searchTerm.value, location.value)
}
</script>

<style scoped>
/* ========== 保留你原来的全部样式 ========== */
.petfinder-container {
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, sans-serif;
  background-color: #f8f9fa;
}

.main-header {
  background-color: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.logo-text {
  font-size: 1.8rem;
  font-weight: bold;
  color: #6a0dad;
  text-transform: lowercase;
}

.nav-menu {
  display: flex;
  list-style: none;
  gap: 2rem;
  margin: 0;
  padding: 0;
}

.nav-link {
  text-decoration: none;
  color: #333;
  font-size: 0.9rem;
  font-weight: 500;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: #6a0dad;
}

.issue-link {
  color: #666;
  font-size: 0.85rem;
  max-width: 200px;
  line-height: 1.2;
}

/* avatar 的外层 */
.avatar-wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;
}

/* 搜索等样式 --- 保留你的 */
.search-bar { background-color: #f0f0f0; padding: 1rem 2rem; }
.search-container { display: flex; gap: 1rem; max-width: 800px; margin: 0 auto; }
.search-input, .location-input {
  flex: 1; padding: 0.8rem 1rem; border: 1px solid #ddd; border-radius: 4px; font-size: 0.9rem;
}
.search-btn {
  background-color: #6a0dad; color: white; border: none; padding: 0.8rem 2rem;
  border-radius: 4px; cursor: pointer; font-weight: 500;
}

.main-content { position: relative; min-height: calc(100vh - 140px); display: flex;
  align-items: center; justify-content: center; padding: 2rem; }

.pet-background {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(rgba(106, 13, 173, 0.1), rgba(106, 13, 173, 0.05));
  z-index: 1;
}

.background-overlay {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  background: url('@/assets/image.png') center/cover no-repeat;
  opacity: 0.4;
}

.hero-content {
  position: relative; z-index: 2; text-align: center; max-width: 800px;
  background-color: rgba(255, 255, 255, 0.95); padding: 3rem; border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.main-title { font-size: 2.5rem; color: #333; margin-bottom: 1rem; line-height: 1.2; font-weight: 700; }
.subtitle { font-size: 1.1rem; color: #666; margin-bottom: 2.5rem; line-height: 1.5; }

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 1.2rem;
  flex-wrap: wrap;
  margin-top: 1.5rem;
}

/* 基础样式 */
.action-button {
  padding: 1rem 2rem;
  border: 2px solid #6a0dad;
  background-color: #ffffff;
  color: #6a0dad;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  min-width: 180px;
  transition: all 0.25s ease;
  letter-spacing: 0.5px;
}

/* 悬停：普通按钮 */
.action-button:hover {
  background-color: #6a0dad;
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 6px 14px rgba(106, 13, 173, 0.25);
}

/* primary 默认样式 */
.action-button.primary {
  background-color: #6a0dad;
  color: white;
  border-color: #6a0dad;
}

/* primary 悬停 */
.action-button.primary:hover {
  background-color: #7d27c9;
  border-color: #7d27c9;
  transform: translateY(-3px);
  box-shadow: 0 6px 14px rgba(106, 13, 173, 0.25);
}


</style>
