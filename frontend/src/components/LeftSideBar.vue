<template>
  <!-- 左侧导航 -->
  <aside class="sidebar">
    <div class="sidebar-header">
      <h2 class="title">animal-shelter</h2>
      <p class="subtitle">分享社区</p>
    </div>

    <el-menu
      :default-active="activeMenu"
      background-color="transparent"
      text-color="#fff"
      active-text-color="#ffd04b"
      class="sidebar-menu"
    >
      <el-menu-item index="1" @click="go('/sharing')">
        <el-icon><Promotion /></el-icon>
        <span>发现</span>
      </el-menu-item>

      <el-menu-item index="2" @click="go('/community/publish')">
        <el-icon><Edit /></el-icon>
        <span>发布</span>
      </el-menu-item>

      <el-menu-item index="3" @click="go('/community/mypost')">
        <el-icon><Bell /></el-icon>
        <span>我的分享</span>
      </el-menu-item>
    </el-menu>
  </aside>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Promotion, Edit, Bell } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = ref('2') // 发布页面默认激活发布菜单

const go = (path) => {
  router.push(path)
}
</script>

<style scoped>
.sidebar {
  width: 240px;
  min-height: 100vh;
  padding: 20px 0;
  background: linear-gradient(180deg, #7e57c2, #9575cd);
  color: #fff;
  box-shadow: 2px 0 10px rgba(126, 87, 194, 0.2);
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 0 20px 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu .el-menu-item {
  height: 56px;
  line-height: 56px;
  font-size: 16px;
  margin: 0 8px;
  border-radius: 12px;
  transition: all 0.3s;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.15) !important;
  font-weight: 600;
}

.sidebar-menu .el-icon {
  font-size: 20px;
  margin-right: 12px;
}
</style>