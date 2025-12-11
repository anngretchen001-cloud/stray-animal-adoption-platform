<template>
  <div class="profile-page">
    <!-- 背景光点 -->
    <div class="bg-dot dot1"></div>
    <div class="bg-dot dot2"></div>
    <div class="bg-dot dot3"></div>

    <el-card class="profile-card">
      <!-- 返回首页按钮 -->
      <el-button class="btn-home" @click="goHome">
        返回首页
      </el-button>

      <h2 class="profile-title">个人信息</h2>

      <el-form :model="userForm" label-width="80px">
        <!-- 头像 -->
        <el-form-item class="avatar-item">
          <el-tooltip content="点击更换头像" placement="top">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :http-request="customUpload"
            >
              <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar" />
              <i v-else class="el-icon-plus avatar-placeholder"></i>
            </el-upload>
          </el-tooltip>
        </el-form-item>

        <!-- 用户名 -->
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" />
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" />
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone" />
        </el-form-item>

        <!-- 角色 -->
        <el-form-item label="角色">
          <el-tag type="success">{{ userStore.role }}</el-tag>
        </el-form-item>

        <!-- 保存按钮 -->
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </el-form-item>

        <!-- 查看我的申请按钮 -->
        <el-form-item>
          <el-button class="btn-applications" @click="goApplications">
            查看我的申请
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCurrentUser, updateProfile } from '@/api/user'
import { uploadFile } from '@/api/file'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = '/default-avatar.jpg'

const userForm = reactive({
  username: '',
  email: '',
  phone: '',
  avatar: ''
})

async function loadUser() {
  try {
    const res = await getCurrentUser()
    const data = res.data.data
    userForm.username = data.username
    userForm.email = data.email
    userForm.phone = data.phone
    userForm.avatar = data.avatarUrl || defaultAvatar
  } catch (err) {
    console.error('获取用户信息失败', err)
  }
}

function beforeUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isJPG) ElMessage.error('头像图片只能是 JPG/PNG 格式!')
  if (!isLt10M) ElMessage.error('头像图片大小不能超过 10MB!')
  return isJPG && isLt10M
}

async function customUpload({ file, onSuccess, onError }) {
  try {
    const res = await uploadFile(file)
    const url = res.data.data
    userForm.avatar = url
    await updateProfile({ avatarUrl: url })
    userStore.setAvatar(url)
    onSuccess(res)
    ElMessage.success('头像上传成功')
  } catch (err) {
    console.error('头像上传失败', err)
    onError(err)
    ElMessage.error('头像上传失败')
  }
}

async function saveProfile() {
  try {
    await updateProfile({
      username: userForm.username,
      email: userForm.email,
      phone: userForm.phone,
      avatarUrl: userForm.avatar
    })
    userStore.setUsername(userForm.username)
    userStore.setAvatar(userForm.avatar)
    ElMessage.success('更新成功')
  } catch (err) {
    console.error('更新失败', err)
    ElMessage.error('更新失败')
  }
}

// 返回首页
function goHome() {
  router.push('/home')
}

// 跳转我的申请页面
function goApplications() {
  router.push('/applications')
}

onMounted(() => {
  loadUser()
})
</script>

<style scoped>
/* 原有样式保持不变 */
.profile-page { min-height: 100vh; display: flex; 
  align-items: center; justify-content: center; 
  background: radial-gradient(ellipse at center, #fff5f8 0%, #f7e9f7 100%);
   position: relative; overflow: hidden; } .bg-dot { position: absolute; border-radius: 50%; opacity: 0.12; z-index: 0; animation: float 8s infinite alternate; } .dot1 { width: 120px; height: 120px; background: #e8c0e8; top: 10%; left: 10%; } .dot2 { width: 80px; height: 80px; background: #e0b7e0; bottom: 15%; right: 15%; } .dot3 { width: 60px; height: 60px; background: #f0d0f0; top: 20%; right: 20%; } @keyframes float { 0% { transform: translateY(0px) translateX(0px); } 50% { transform: translateY(-12px) translateX(8px); } 100% { transform: translateY(0px) translateX(0px); } } .profile-card { max-width: 480px; width: 100%; padding: 36px; border-radius: 24px; background: linear-gradient(135deg, rgba(230,200,230,0.15), rgba(230,190,230,0.08)); box-shadow: 0 20px 40px rgba(230,190,230,0.25); border: 1px solid rgba(230,190,230,0.2); position: relative; z-index: 1; transition: transform 0.3s ease; } .profile-card:hover { transform: translateY(-4px); } .btn-home { position: absolute; top: 16px; right: 16px; background: linear-gradient(90deg, #d8a5d8, #c78ac7); color: #fff; border-radius: 12px; font-weight: 600; padding: 6px 16px; box-shadow: 0 2px 8px rgba(216,165,216,0.3); transition: all 0.3s ease; z-index: 2; } .btn-home:hover { background: linear-gradient(90deg, #e0b2e0, #d09ad0); box-shadow: 0 4px 12px rgba(216,165,216,0.4); } /* 头像、表单、按钮样式保持原样 */ .el-form { display: flex; flex-direction: column; align-items: center; } .el-form-item { width: 100%; margin-bottom: 20px; } .el-form-item__label { text-align: left; color: #b18ab1; font-weight: 600; } .el-input__inner { border-radius: 12px; text-align: left; padding: 10px 12px; } .avatar { width: 100px; height: 100px; border-radius: 12px; cursor: pointer; margin-bottom: 20px; transition: all 0.3s ease; } .avatar:hover { transform: scale(1.05); box-shadow: 0 4px 15px rgba(200,150,200,0.3); } .avatar-placeholder { display: inline-block; width: 100px; height: 100px; border: 2px dashed #c8a5c8; border-radius: 12px; line-height: 100px; text-align: center; font-size: 36px; color: #c8a5c8; cursor: pointer; margin-bottom: 20px; } .el-button--primary { width: 100%; height: 46px; font-weight: 600; border-radius: 12px; background: linear-gradient(90deg, #d8a5d8, #c78ac7) !important; color: #fff; border: none !important; box-shadow: 0 2px 8px rgba(216,165,216,0.3); } .el-button--primary:hover { background: linear-gradient(90deg, #e0b2e0, #d09ad0) !important; box-shadow: 0 4px 12px rgba(216,165,216,0.4); } .el-tag { font-weight: 600; background: #e8c0e8; color: #9c6c9c; border-radius: 12px; padding: 4px 12px; }

.btn-applications {
  width: 100%;
  height: 46px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(90deg, #cfa0e0, #b881d1);
  color: #fff;
  border: none;
  box-shadow: 0 2px 8px rgba(200,150,200,0.3);
  transition: all 0.3s ease;
}

.btn-applications:hover {
  background: linear-gradient(90deg, #d3a9e3, #bc85d6);
  box-shadow: 0 4px 12px rgba(200,150,200,0.4);
}
</style>
