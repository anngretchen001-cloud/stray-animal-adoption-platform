<template>
  <nav class="admin-navbar">
    <div class="left">管理员后台</div>

    <div class="right">
      <el-button type="primary" @click="goHome">返回前台</el-button>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </div>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

function goHome() {
  router.push('/home')
}

function logout() {
  userStore.logout()
  ElMessage.success('已退出')
  router.push('/login')
}
</script>

<style scoped>
.admin-navbar {
  height: 60px;
  background: #6a0dad;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.left {
  font-size: 20px;
  font-weight: bold;
}

.right {
  display: flex;
  gap: 10px;
}
</style>
