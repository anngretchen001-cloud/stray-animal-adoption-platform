<template>
  <div class="register-page">
    <el-card class="register-card" shadow="hover">
      <h1 class="register-title">Pet Shelter Register</h1>

      <!-- 选项卡 -->
      <el-tabs v-model="activeTab" type="card" class="register-tabs" tab-position="top">
        <el-tab-pane label="个人注册" name="personal">
          <el-form :model="personalForm" label-position="top" class="register-form">
            <el-form-item label="Username">
              <el-input v-model="personalForm.username" placeholder="Enter username" clearable />
            </el-form-item>
            <el-form-item label="Password">
              <el-input type="password" v-model="personalForm.password" placeholder="Enter password" show-password clearable />
            </el-form-item>
            <el-form-item label="Email">
              <el-input v-model="personalForm.email" placeholder="Enter email" clearable />
            </el-form-item>
            <el-form-item label="Phone">
              <el-input v-model="personalForm.phone" placeholder="Enter phone" clearable />
            </el-form-item>
            <el-button type="primary" class="submit-btn" @click="submitPersonal">Register</el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="救助组织注册" name="organization">
          <el-form :model="orgForm" label-position="top" class="register-form">
            <h3 class="form-section">User Info</h3>
            <el-form-item label="Username">
              <el-input v-model="orgForm.user.username" placeholder="Enter username" clearable />
            </el-form-item>
            <el-form-item label="Password">
              <el-input type="password" v-model="orgForm.user.password" placeholder="Enter password" show-password clearable />
            </el-form-item>
            <el-form-item label="Email">
              <el-input v-model="orgForm.user.email" placeholder="Enter email" clearable />
            </el-form-item>
            <el-form-item label="Phone">
              <el-input v-model="orgForm.user.phone" placeholder="Enter phone" clearable />
            </el-form-item>

            <h3 class="form-section">Organization Info</h3>
            <el-form-item label="Name">
              <el-input v-model="orgForm.organization.name" placeholder="Organization name" clearable />
            </el-form-item>
            <el-form-item label="Certificate No">
              <el-input v-model="orgForm.organization.certificateNo" placeholder="Certificate number" clearable />
            </el-form-item>
            <el-form-item label="Contact Name">
              <el-input v-model="orgForm.organization.contactName" placeholder="Contact person" clearable />
            </el-form-item>
            <el-form-item label="Email">
              <el-input v-model="orgForm.organization.email" placeholder="Organization email" clearable />
            </el-form-item>
            <el-form-item label="Phone">
              <el-input v-model="orgForm.organization.phone" placeholder="Organization phone" clearable />
            </el-form-item>
            <el-form-item label="Address">
              <el-input v-model="orgForm.organization.address" placeholder="Organization address" clearable />
            </el-form-item>
            <el-form-item label="Description">
              <el-input v-model="orgForm.organization.description" placeholder="Brief description" type="textarea" />
            </el-form-item>

            <el-button type="primary" class="submit-btn" @click="submitOrganization">Register Organization</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <p class="login-link">Already have an account? <span @click="goLogin">Login</span></p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register, registerOrganization } from '@/api/user'

const router = useRouter()
const activeTab = ref('personal')

const personalForm = reactive({
  username: '',
  password: '',
  email: '',
  phone: ''
})

const orgForm = reactive({
  user: { username: '', password: '', email: '', phone: '' },
  organization: { name: '', certificateNo: '', contactName: '', email: '', phone: '', address: '', description: '' }
})

// 个人注册
async function submitPersonal() {
  if (!personalForm.username || !personalForm.password) return
  try {
    // 直接传 personalForm，不用包一层 user
    await register(personalForm)
    alert('注册成功')
    router.push('/login')
  } catch (e) {
    console.error(e)
    alert(e.response?.data?.message || '注册失败')
  }
}

// 组织注册
async function submitOrganization() {
  if (!orgForm.user.username || !orgForm.user.password) return
  try {
    // 确保传的结构跟后端 DTO 一致
    const payload = {
      user: { ...orgForm.user },
      organization: { ...orgForm.organization }
    }
    await registerOrganization(payload)
    alert('组织注册成功，等待管理员审核')
    router.push('/login')
  } catch (e) {
    console.error(e)
    alert(e.response?.data?.message || '组织注册失败')
  }
}


function goLogin() {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(ellipse at center, #f8f5ff 0%, #e9e0ff 100%);
}

.register-card {
  width: 500px;
  padding: 32px;
  border-radius: 24px;
  box-shadow: 0 16px 40px rgba(124,58,237,0.08);
  background: linear-gradient(135deg, rgba(124,58,237,0.08), rgba(99,102,241,0.05));
}

.register-title {
  font-size: 2rem;
  font-weight: 800;
  color: #4c1d95;
  text-align: center;
  margin-bottom: 12px;
}

/* 圆角选项卡 */
.register-tabs ::v-deep(.el-tabs__header) {
  border-radius: 12px;
  background: #f3ebff;
}

.register-tabs ::v-deep(.el-tabs__item) {
  border-radius: 12px;
  font-weight: 600;
  color: #6d4b9f;
}

.register-tabs ::v-deep(.el-tabs__item.is-active) {
  background: #dcd0ff;
  color: #4c1d95;
}

.register-form {
  margin-top: 12px;
}

.form-section {
  font-weight: 600;
  color: #7c5fc5;
  margin-top: 16px;
  margin-bottom: 8px;
}

.submit-btn {
  margin-top: 16px;
  width: 100%;
  height: 46px;
  border-radius: 12px;
  font-weight: 600;
  background: linear-gradient(90deg, #7c3aed, #6d28d9);
  color: #fff;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background: linear-gradient(90deg, #9246f0, #8042d9);
  transform: translateY(-2px);
}

.login-link {
  text-align: center;
  margin-top: 16px;
  color: #7c3aed;
}

.login-link span {
  cursor: pointer;
  font-weight: 600;
}
</style>
