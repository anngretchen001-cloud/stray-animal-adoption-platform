<template>
  <div class="applications-page">
    <div class="header">
      <h1 class="title">我的领养申请</h1>
      <el-button type="primary" @click="goProfile" class="btn-profile">返回个人信息</el-button>
    </div>

    <div v-if="applications.length" class="cards-container">
      <el-card
        v-for="app in applications"
        :key="app.id"
        class="application-card"
        shadow="hover"
      >
        <div class="card-header">
          <h2 class="animal-name">{{ app.animalName || '未知宠物' }}</h2>
          <el-tag :type="statusType(app.status)" class="status-tag">
            {{ app.status }}
          </el-tag>
        </div>

        <div class="card-body">
          <p><strong>品种：</strong>{{ app.breed || '-' }}</p>
          <p><strong>年龄：</strong>{{ app.age || '-' }}</p>
          <p><strong>留言：</strong>{{ app.reason || '无' }}</p>
          <p><strong>申请时间：</strong>{{ app.createdAt }}</p>
        </div>

        <div class="card-footer">
          <el-button
            v-if="app.status.toLowerCase() === 'pending'"
            type="danger"
            size="small"
            @click="cancel(app.id)"
            class="btn-cancel"
          >
            取消申请
          </el-button>
        </div>
      </el-card>
    </div>

    <div v-else class="empty">
      😿 你还没有提交任何领养申请
    </div>

    <el-pagination
      v-if="total > pageSize"
      class="pagination"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      :current-page="pageNum"
      @current-change="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyApplications, cancelAdoption } from '@/api/application'

const router = useRouter()
const applications = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function loadApplications() {
  try {
    const res = await getMyApplications(pageNum.value, pageSize.value)
    if (res.data && res.data.data) {
      applications.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    } else {
      applications.value = []
      total.value = 0
    }
  } catch (err) {
    console.error('加载我的申请失败：', err)
    applications.value = []
  }
}

function handlePageChange(p) {
  pageNum.value = p
  loadApplications()
}

function statusType(status) {
  switch (status.toLowerCase()) {
    case 'pending':
      return 'warning'
    case 'approved':
      return 'success'
    case 'rejected':
      return 'danger'
    default:
      return 'info'
  }
}

function goProfile() {
  router.push('/profile')
}

async function cancel(applicationId) {
  try {
    await cancelAdoption(applicationId)
    loadApplications()
  } catch (err) {
    console.error('取消失败：', err)
  }
}

onMounted(() => loadApplications())
</script>

<style scoped>
.applications-page {
  padding: 4rem 2rem 2rem;
  background: linear-gradient(to bottom, #fff0f5, #fdeef8);
  min-height: 100vh;
  font-family: 'Segoe UI', sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.title {
  font-size: 2rem;
  font-weight: bold;
  color: #a35cb5;
}

.btn-profile {
  background: linear-gradient(90deg, #f7b2d9, #d38ac7);
  color: #fff;
  border-radius: 12px;
  font-weight: 600;
}

.cards-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
}

.application-card {
  width: 100%;
  max-width: 460px;
  border-radius: 16px;
  background: #fffafc;
  padding: 1.5rem;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.application-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 25px rgba(200, 150, 200, 0.25);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.animal-name {
  font-size: 1.3rem;
  font-weight: 700;
  color: #8a2be2;
}

.status-tag {
  font-weight: 600;
  border-radius: 12px;
  padding: 4px 12px;
}

.card-body p {
  margin: 0.25rem 0;
  color: #5a3d70;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
}

.btn-cancel {
  background: linear-gradient(90deg, #ffb2b2, #ff7c7c);
  color: #fff;
  border-radius: 12px;
  font-weight: 600;
}

.btn-cancel:hover {
  background: linear-gradient(90deg, #ff9a9a, #ff5c5c);
}

.empty {
  text-align: center;
  font-size: 1.2rem;
  color: #c78ac7;
  margin-top: 3rem;
}

.pagination {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}
</style>
