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
            <el-table-column prop="status" label="审核状态">
              <template #default="{ row }">
                <el-tag 
                  :type="getStatusTagType(row.status)" 
                  size="small"
                >
                  {{ formatStatus(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="enabled" label="启用状态">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'danger'">
                  {{ row.enabled ? '正常' : '封禁中' }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <!-- 修复这里：兼容大小写 -->
                <el-button
                  v-if="row.status === 'PENDING' || row.status === 'pending'"
                  type="success"
                  size="small"
                  @click="approveOrg(row)"
                >
                  通过
                </el-button>

                <el-button
                  v-if="row.status === 'PENDING' || row.status === 'pending'"
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
  console.log('组织管理数据:', res.data.data)  // 添加调试
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

// 添加状态格式化函数
function formatStatus(status) {
  if (!status) return '未知'
  const upperStatus = status.toUpperCase()
  if (upperStatus === 'PENDING') return '待审核'
  if (upperStatus === 'APPROVED') return '已通过'
  if (upperStatus === 'REJECTED') return '已拒绝'
  return status
}

function getStatusTagType(status) {
  if (!status) return 'info'
  const upperStatus = status.toUpperCase()
  if (upperStatus === 'PENDING') return 'warning'
  if (upperStatus === 'APPROVED') return 'success'
  if (upperStatus === 'REJECTED') return 'danger'
  return 'info'
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
  background: #f8fafc;
}

/* 主体区域：左侧菜单 + 内容 */
.admin-main {
  display: flex;
  flex: 1;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

/* 左侧菜单 */
.sidebar {
  width: 220px;
  background: white;
  padding: 20px;
  border-right: 1px solid #e2e8f0;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  flex-shrink: 0;
}

.sidebar-title {
  font-weight: 700;
  font-size: 1.2rem;
  margin-bottom: 25px;
  color: #5a2e8a;
  padding-bottom: 15px;
  border-bottom: 2px solid #f1f5f9;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu-item {
  padding: 12px 15px;
  border-radius: 10px;
  cursor: pointer;
  background: #f8fafc;
  color: #64748b;
  transition: all 0.3s ease;
  font-weight: 500;
  border: 1px solid transparent;
}

.menu-item:hover {
  background: #f1f5f9;
  color: #475569;
  border-color: #e2e8f0;
  transform: translateX(5px);
}

.menu-item.active {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  font-weight: 600;
  color: white;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
  border-color: #7c3aed;
}

/* 右侧内容区 */
.content-area {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

.card {
  background: white;
  padding: 25px;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  min-height: calc(100vh - 100px);
}

.card h2 {
  margin: 0 0 20px 0;
  color: #1e293b;
  font-size: 1.8rem;
  font-weight: 700;
  padding-bottom: 20px;
  border-bottom: 2px solid #f1f5f9;
}
</style>