<template>
  <div class="admin-layout">
    
    <!-- 顶部导航栏 -->
    <AdminNavbar />

    <div class="admin-main">
      
      <!-- 左侧菜单 -->
      <aside class="sidebar">
        <div class="sidebar-title">后台菜单</div>

        <div class="menu-list">
          <div
            :class="['menu-item', currentTab === 'users' ? 'active' : '']"
            @click="currentTab = 'users'"
          >
            用户管理
          </div>

          <div
            :class="['menu-item', currentTab === 'organizations' ? 'active' : '']"
            @click="currentTab = 'organizations'"
          >
            组织管理
          </div>
        </div>
      </aside>

      <!-- 右侧内容 -->
      <section class="content-area">
        
        <!-- 用户管理 -->
        <div v-if="currentTab === 'users'" class="card">
          <h2>用户管理</h2>

          <el-table :data="users" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="enabled" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'danger'">
                  {{ row.enabled ? '正常' : '封禁中' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button
                  size="small"
                  type="warning"
                  @click="toggleUser(row)"
                >
                  {{ row.enabled ? '封禁' : '解禁' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 组织管理 -->
        <div v-if="currentTab === 'organizations'" class="card">
          <h2>组织管理</h2>

          <el-table :data="organizations" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="组织名" />
            <el-table-column prop="status" label="审核状态" />
            <el-table-column prop="enabled" label="启用状态">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'danger'">
                  {{ row.enabled ? '正常' : '封禁中' }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="220">
              <template #default="{ row }">

                <el-button
                  v-if="row.status === 'PENDING'"
                  type="success"
                  size="small"
                  @click="approveOrg(row)"
                >
                  通过
                </el-button>

                <el-button
                  v-if="row.status === 'PENDING'"
                  type="danger"
                  size="small"
                  @click="rejectOrg(row)"
                >
                  拒绝
                </el-button>

                <el-button
                  size="small"
                  type="warning"
                  @click="toggleOrg(row)"
                >
                  {{ row.enabled ? '封禁' : '解禁' }}
                </el-button>

              </template>
            </el-table-column>
          </el-table>
        </div>

      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AdminNavbar from '@/components/AdminNavbar.vue'

import {
  listUsers,
  listOrganizations,
  approveOrganization,
  rejectOrganization,
  setUserEnabled,
  setOrganizationEnabled
} from '@/api/admin'

import { ElMessage } from 'element-plus'

const currentTab = ref('users')

const users = ref([])
const organizations = ref([])

async function loadUsers() {
  const res = await listUsers()
  users.value = res.data.data
}

async function loadOrganizations() {
  const res = await listOrganizations()
  organizations.value = res.data.data
}

function toggleUser(u) {
  setUserEnabled(u.id, !u.enabled).then(() => {
    ElMessage.success("操作成功")
    loadUsers()
  })
}

function toggleOrg(o) {
  setOrganizationEnabled(o.id, !o.enabled).then(() => {
    ElMessage.success("操作成功")
    loadOrganizations()
  })
}

function approveOrg(o) {
  approveOrganization(o.id).then(() => {
    ElMessage.success("审核通过")
    loadOrganizations()
  })
}

function rejectOrg(o) {
  rejectOrganization(o.id).then(() => {
    ElMessage.success("审核拒绝")
    loadOrganizations()
  })
}

onMounted(() => {
  loadUsers()
  loadOrganizations()
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* 主体区域：左侧菜单 + 内容 */
.admin-main {
  display: flex;
  flex: 1;
  background: #f7f5fb;
}

/* 左侧菜单 */
.sidebar {
  width: 200px;
  background: #e7ddf5;
  padding: 20px;
  border-radius: 0 20px 20px 0;
}

.sidebar-title {
  font-weight: bold;
  margin-bottom: 15px;
  color: #5a2ea6;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.menu-item {
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  background: #f5effc;
  color: #5a2ea6;
  transition: 0.2s;
  text-align: center;
}

.menu-item:hover {
  background: #d9c7f0;
}

.menu-item.active {
  background: #bba2e6;
  font-weight: bold;
  color: white;
}

/* 右侧内容区 */
.content-area {
  flex: 1;
  padding: 25px;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}
</style>
