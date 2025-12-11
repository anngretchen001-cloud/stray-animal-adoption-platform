<template>
  <nav class="navbar">
    <div class="nav-left">
      <div class="logo" @click="goHome" style="cursor: pointer;">
        <span class="logo-text">Animal-shelter</span>
      </div>

      <ul class="nav-menu">
        <li class="nav-item">
          <router-link to="/" class="nav-link">FIND A PET</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/" class="nav-link">ALL ABOUT PETS</router-link>
        </li>
        <li class="nav-item">
          <a href="#" class="nav-link issue-link">
            Experiencing site issues? We're on it- See Updates and Tips.
          </a>
        </li>
      </ul>
    </div>

    <div class="nav-right">
      <el-dropdown>
        <span class="avatar-wrapper">
          <el-avatar :src="userStore.avatar || defaultAvatar" size="medium" />
        </span>

        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goProfile">
              个人信息
            </el-dropdown-item>

            <el-dropdown-item
              v-if="['ORG','ADMIN'].includes(userStore.role)"
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
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const defaultAvatar = '/default-avatar.png'

function goHome() {
  router.push('/')
}
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
</script>

<style scoped>
/* 直接复用你的样式 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
.nav-left { display: flex; align-items: center; gap: 2rem; }
.logo-text { font-size: 1.8rem; font-weight: bold; color: #6a0dad; }
.nav-menu { display: flex; gap: 2rem; list-style: none; }
.nav-link { color: #333; text-decoration: none; }
.nav-link:hover { color: #6a0dad; }
</style>
