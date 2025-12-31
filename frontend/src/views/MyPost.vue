<template>
  <NavBar />

  <div class="community-page">
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

    <!-- 右侧内容 -->
    <main class="content">
      <!-- 搜索 & 分类 -->
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索我发布的文章"
          clearable
          :prefix-icon="Search"
          @keyup.enter="fetchPosts"
          @clear="fetchPosts"
        />

        <div class="categories">
          <el-tag
            v-for="c in categories"
            :key="c.value"
            :type="c.value === type ? 'primary' : 'info'"
            class="tag"
            @click="changeType(c.value)"
          >
            {{ c.label }}
          </el-tag>
        </div>
      </div>

      <!-- 内容区 -->
      <el-skeleton v-if="loading" :rows="6" animated />

      <el-empty
        v-else-if="!posts || posts.length === 0"
        description="你还没有发布过文章"
      />

      <el-row v-else :gutter="20">
        <el-col
          v-for="post in posts"
          :key="post.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card
            class="post-card"
            shadow="hover"
            @click="goDetail(post.id)"
          >
            <img
              class="cover"
              :src="post.coverUrl || defaultImg"
              @error="imgError"
            />

            <div class="card-body">
              <h3 class="title">{{ post.title }}</h3>
              <div class="meta">
                <el-avatar
                  size="small"
                  :src="post.authorAvatar || defaultAuthImg"
                />
                <span class="author">{{ post.authorName || '我' }}</span>
                <span class="views">👁 {{ post.viewCount || 0 }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NavBar from '@/components/NavBar.vue'
import { pagePosts } from '@/api/post'
import { Search, Promotion, Edit, Bell } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

/* 左侧导航 */
const activeMenu = ref('3')
const go = (path) => router.push(path)

/* 查询条件 */
const keyword = ref('')
const type = ref('')
const pageNum = ref(1)
const pageSize = ref(12)

/* 分类 */
const categories = [
  { label: '全部', value: '' },
  { label: '公告', value: 'ANNOUNCEMENT' },
  { label: '科普', value: 'POPULAR_SCIENCE' },
  { label: '求助', value: 'HELP' },
  { label: '分享', value: 'SHARE' }
]

/* 数据 */
const loading = ref(false)
const posts = ref([])
const defaultImg = '/default-pet.jpg'
const defaultAuthImg = '/default-avatar.jpg'

const changeType = (val) => {
  type.value = val
  pageNum.value = 1
  fetchPosts()
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const page = await pagePosts({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value,
      type: type.value,
      userId: userStore.userId// ⭐ 关键：只查当前用户的
    })
    posts.value = page.records || []
  } catch (err) {
    console.error(err)
    posts.value = []
  } finally {
    loading.value = false
  }
}

const goDetail = (id) => {
  router.push(`/community/${id}`)
}

const imgError = (e) => {
  e.target.src = defaultImg
}

onMounted(fetchPosts)
</script>

<style scoped>
.community-page {
  display: flex;
  background: #f6f3fb;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 240px;
  padding: 20px;
  background: linear-gradient(180deg, #7e57c2, #9575cd);
  border-radius: 0 24px 24px 0;
  color: #fff;
}

.content {
  flex: 1;
  margin: 20px;
  padding: 24px;
  background: #fff;
  border-radius: 24px;
}

.toolbar {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.categories {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag {
  cursor: pointer;
  border-radius: 14px;
}

.post-card {
  border-radius: 18px;
  overflow: hidden;
  cursor: pointer;
}

.cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.card-body {
  padding: 12px;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #888;
}

.meta .author {
  margin-left: 4px;
}
</style>
