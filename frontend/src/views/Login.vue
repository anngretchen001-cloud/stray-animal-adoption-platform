<template>
  <div class="login-page">
    <el-card class="login-card" shadow="hover">
      <h1 class="login-title">Pet Shelter</h1>
      <p class="login-subtitle">Find your perfect companion</p>

      <el-form class="login-form" :model="form" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="Username" prop="username">
          <el-input v-model="form.username" placeholder="Enter username" aria-label="Username" clearable />
        </el-form-item>

        <el-form-item label="Password" prop="password">
          <el-input
            v-model="form.password"
            placeholder="Enter password"
            type="password"
            show-password
            aria-label="Password"
            clearable
          />
        </el-form-item>

        <!-- 横向按钮 -->
        <div class="form-actions">
          <el-button type="primary" class="action-btn" @click="handleLogin">Login</el-button>
          <el-button type="default" class="action-btn ghost" @click="goRegister">Register</el-button>
        </div>
      </el-form>

      <p class="login-footer">© 2025 Pet Shelter. All rights reserved.</p>
    </el-card>

    <!-- 装饰光点 -->
    <div class="bg-dot dot1"></div>
    <div class="bg-dot dot2"></div>
    <div class="bg-dot dot3"></div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/user'
import api from '@/api/index'  




const router = useRouter()
const userStore = useUserStore()

const form = reactive({ username: '', password: '' })


async function handleLogin() {
  if (!form.username || !form.password) return
  try {
    const res = await login({ username: form.username, password: form.password })
    const token = res.data.data.token

    // 存 token
    userStore.setToken(token)

    // 用 token 调 /api/users/me 获取角色
    const me = await api.get('/users/me', {
      headers: { Authorization: `Bearer ${token}` }
    })

    const userInfo = me.data.data  // ← 关键，这里才是实际用户信息
    userStore.setRole(userInfo.role)
    userStore.setUsername(userInfo.username)
    userStore.setAvatar(userInfo.avatarUrl || '')
    console.log("login response:", res)
    console.log("role:", userInfo.role)

    router.push('/home')
  } catch (e) {
    console.error(e)
  }
}


function goRegister() {
  router.push('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(ellipse at center, #f8f5ff 0%, #e9e0ff 100%);
  position: relative;
  overflow: hidden;
}

.login-card {
  width: 400px;
  padding: 36px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(124,58,237,0.08), rgba(99,102,241,0.05));
  box-shadow: 0 20px 50px rgba(124,58,237,0.12);
  border: 1px solid rgba(124,58,237,0.08);
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.login-card:hover {
  transform: translateY(-4px);
}

.login-title {
  font-size: 2rem;
  font-weight: 800;
  text-align: center;
  color: #4c1d95;
  margin-bottom: 6px;
}

.login-subtitle {
  text-align: center;
  font-size: 0.95rem;
  color: #7c3aed;
  margin-bottom: 28px;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.el-form-item__label {
  text-align: left;
  color: #5b3aa3;
  font-weight: 600;
}

.el-input__inner {
  border-radius: 12px;
  text-align: left;
  padding: 10px 12px;
}

/* 横向按钮布局 */
.form-actions {
  display: flex;
  gap: 12px; /* 按钮间距 */
  margin-top: 20px;
}

.action-btn {
  flex: 1; /* 等宽 */
  height: 46px;
  font-weight: 600;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.action-btn:hover {
  transform: translateY(-2px);
}

.form-actions .el-button--primary {
  background: linear-gradient(90deg, #7c3aed, #6d28d9) !important;
  color: #fff;
  border: none !important;
  box-shadow: 0 2px 8px rgba(124,58,237,0.2);
}

.form-actions .el-button--primary:hover {
  background: linear-gradient(90deg, #9246f0, #8042d9) !important;
  box-shadow: 0 4px 12px rgba(124,58,237,0.3);
}

.el-button.ghost {
  background: transparent;
  border: 1px solid rgba(124,58,237,0.3);
  color: #5b3aa3;
}

.el-button.ghost:hover {
  background: rgba(124,58,237,0.08);
  border-color: rgba(124,58,237,0.5);
  color: #6d28d9;
}

.login-footer {
  font-size: 0.8rem;
  text-align: center;
  color: #9f7aea;
  margin-top: 20px;
}

/* 背景光点 */
.bg-dot {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
  z-index: 0;
  animation: float 8s infinite alternate;
}

.dot1 {
  width: 120px;
  height: 120px;
  background: #7c3aed;
  top: 15%;
  left: 10%;
}

.dot2 {
  width: 80px;
  height: 80px;
  background: #9333ea;
  bottom: 15%;
  right: 15%;
}

.dot3 {
  width: 60px;
  height: 60px;
  background: #c084fc;
  top: 10%;
  right: 20%;
}

@keyframes float {
  0% { transform: translateY(0px) translateX(0px); }
  50% { transform: translateY(-12px) translateX(8px); }
  100% { transform: translateY(0px) translateX(0px); }
}

</style>
