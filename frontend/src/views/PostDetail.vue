<template>
  <div class="post-detail-page">
    <NavBar />

    <div class="post-container" v-if="post">
      <!-- 文章标题和操作按钮 -->
      <div class="post-header">
        <h1>{{ post.title }}</h1>
        <div v-if="Number(post.authorId) === Number(userStore.userId)" class="post-actions">
          <!-- 编辑按钮 -->
          <button
            @click="handleEdit"
            class="edit-btn"
          >
            <i class="el-icon-edit"></i>
            <span>编辑</span>
          </button>
          
          <!-- 删除按钮 -->
          <button
            @click="handleDeletePost"
            class="delete-post-btn"
          >
            <i class="el-icon-delete"></i>
            <span>删除</span>
          </button>
        </div>
      </div>

      <div class="meta">
        <el-avatar :size="'small'" :src="getImageUrl(post.authorAvatar)" @error="handleAvatarError" />
        <span class="author-name">{{ post.authorName || '匿名用户' }}</span>
        <el-tag class="type-tag" size="small">{{ post.type || '分享' }}</el-tag>
        <span class="view-count">👁 {{ post.viewCount || 0 }} 浏览</span>
      </div>

      <!-- 封面图片容器 -->
      <div v-if="post.coverUrl" class="cover-container">
        <el-image
          :src="post.coverUrl"
          :preview-src-list="[post.coverUrl]"
          fit="contain"
          class="cover-image"
          :lazy="true"
          hide-on-click-modal
          @error="handleCoverError"
        >
          <template #error>
            <div class="image-error">
              <i class="el-icon-picture-outline"></i>
              <span>图片加载失败</span>
            </div>
          </template>
          
          <template #placeholder>
            <div class="image-loading">
              <i class="el-icon-loading"></i>
              <span>加载中...</span>
            </div>
          </template>
        </el-image>
      </div>

      <div class="content" v-html="post.content"></div>

      <!-- 评论区 -->
      <div class="comments-section">
        <h3><i class="el-icon-chat-dot-round"></i> 评论区</h3>

        <!-- 发布评论 -->
        <div class="comment-input-wrapper">
          <el-input
            v-model="newComment"
            type="textarea"
            placeholder="写下你的温暖评论..."
            :rows="3"
            resize="none"
            class="comment-input"
          />
          <el-button
            type="primary"
            size="small"
            @click="submitComment"
            :loading="commentSubmitting"
            class="submit-btn"
            :disabled="!newComment.trim()"
          >
            <i class="el-icon-upload2"></i> 发布评论
          </el-button>
        </div>

        <!-- 评论列表 -->
        <el-skeleton v-if="loadingComments" :rows="3" animated />
        <el-empty v-else-if="!comments.length" description="还没有评论，快来第一个留言吧～" :image-size="120">
          <el-button type="primary" @click="focusCommentInput">我来留言</el-button>
        </el-empty>

        <div v-else class="comments-list">
          <el-card
            v-for="comment in comments"
            :key="comment.id"
            class="comment-card"
            shadow="hover"
          >
            <div class="comment-header">
              <div class="user-info">
                <el-avatar 
                  :size="32" 
                  :src="getImageUrl(comment.authorAvatar)" 
                  @error="handleAvatarError"
                  class="comment-avatar"
                />
                <div class="user-detail">
                  <span class="comment-author">{{ comment.authorName || '匿名用户' }}</span>
                  <span class="time">{{ formatTime(comment.createdAt) }}</span>
                </div>
              </div>
              
              <!-- 修改后的删除按钮 -->
              <button
                v-if="Number(comment.userId) === Number(userStore.userId)"
                @click="handleDeleteComment(comment.id)"
                class="custom-delete-btn"
                >
                <i class="el-icon-delete"></i>
                <span>删除</span>
                </button>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </el-card>
        </div>

        <el-pagination
          v-if="totalComments > pageSize"
          background
          layout="prev, pager, next"
          :page-size="pageSize"
          :current-page="pageNum"
          :total="totalComments"
          @current-change="handlePageChange"
          class="pagination"
        />
      </div>
    </div>

    <el-skeleton v-else :rows="6" animated class="loading-skeleton" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import { getPost, updatePost, deletePost } from '@/api/post'
import { pageComments, createComment, deleteComment } from '@/api/comment'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const post = ref(null)

// 修复：正确的默认图片路径
const defaultImg = 'default-pet.jpg'

// 评论相关
const comments = ref([])
const loadingComments = ref(false)
const commentSubmitting = ref(false)
const newComment = ref('')
const pageNum = ref(1)
const pageSize = ref(5)
const totalComments = ref(0)

// 获取正确的图片URL
const getImageUrl = (imgPath) => {
  if (!imgPath) {
    return `/${defaultImg}`
  }
  if (imgPath.startsWith('http') || imgPath.startsWith('//')) {
    return imgPath
  }
  return imgPath.startsWith('/') ? imgPath : `/${imgPath}`
}

// 图片加载失败处理
const handleAvatarError = (event) => {
  event.target.src = `/${defaultImg}`
  event.target.onerror = null
}

const handleCoverError = (event) => {
  console.error('封面图片加载失败:', event.target.src)
  event.target.style.display = 'none'
}

// 处理富文本内容中的图片
const processContentImages = () => {
  nextTick(() => {
    const contentEl = document.querySelector('.content')
    if (!contentEl) return
    
    const images = contentEl.querySelectorAll('img')
    images.forEach(img => {
      img.addEventListener('error', () => {
        img.style.display = 'none'
      })
      
      // 设置自适应样式
      img.style.maxWidth = '100%'
      img.style.height = 'auto'
      img.style.borderRadius = '8px'
      img.style.margin = '12px auto'
      img.style.display = 'block'
      img.style.boxShadow = '0 4px 12px rgba(126, 87, 194, 0.1)'
      img.style.cursor = 'pointer'
      
      // 添加点击预览功能
      img.addEventListener('click', (e) => {
        e.stopPropagation()
        const viewer = window.ImageViewer || false
        if (viewer) {
          viewer.show(img.src)
        }
      })
    })
  })
}

const fetchPost = async () => {
  try {
    post.value = await getPost(route.params.id)
    fetchComments()
    processContentImages()
  } catch (err) {
    console.error(err)
    ElMessage.error('加载文章失败')
  }
}

const fetchComments = async () => {
  if (!post.value) return
  loadingComments.value = true
  try {
    const res = await pageComments({
      postId: post.value.id,
      pageNum: Number(pageNum.value),
      pageSize: Number(pageSize.value)
    })
    comments.value = res.records || []
    totalComments.value = res.total || 0
  } catch (err) {
    console.error(err)
    comments.value = []
    totalComments.value = 0
    ElMessage.error('加载评论失败')
  } finally {
    loadingComments.value = false
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  if (!userStore.userId) {
    ElMessage.warning('请先登录')
    return
  }
  
  commentSubmitting.value = true
  try {
    await createComment({
      postId: post.value.id,
      content: newComment.value.trim()
    })
    newComment.value = ''
    pageNum.value = 1
    ElMessage.success('评论发布成功')
    fetchComments()
  } catch (err) {
    console.error(err)
    ElMessage.error('评论发布失败')
  } finally {
    commentSubmitting.value = false
  }
}

// 删除评论
const handleDeleteComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger',
      customClass: 'delete-confirm-dialog'
    })
    
    await deleteComment(id)
    comments.value = comments.value.filter(c => c.id !== id)
    totalComments.value = Math.max(0, totalComments.value - 1)
    
    ElMessage.success('评论删除成功')
  } catch (err) {
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('删除失败')
    }
  }
}

const handleEdit = () => {
  router.push(`/community/edit/${post.value.id}`)
}

// 删除文章
const handleDeletePost = async () => {
  try {
    await ElMessageBox.confirm('确定删除这篇文章吗？删除后无法恢复', '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error',
      confirmButtonClass: 'el-button--danger',
      customClass: 'delete-post-dialog',
      center: true
    })
    
    await deletePost(post.value.id)
    ElMessage.success('文章删除成功')
    
    // 删除成功后跳转回文章列表
    setTimeout(() => {
      router.push('/sharing')
    }, 1000)
    
  } catch (err) {
    if (err !== 'cancel') {
      console.error(err)
      ElMessage.error('删除失败')
    }
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchComments()
}

// 时间格式化
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60 * 1000) {
    return '刚刚'
  }
  if (diff < 60 * 60 * 1000) {
    return Math.floor(diff / (60 * 1000)) + '分钟前'
  }
  if (date.toDateString() === now.toDateString()) {
    return Math.floor(diff / (60 * 60 * 1000)) + '小时前'
  }
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (date.toDateString() === yesterday.toDateString()) {
    return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return Math.floor(diff / (24 * 60 * 60 * 1000)) + '天前'
  }
  return date.toLocaleDateString('zh-CN')
}

// 聚焦评论输入框
const focusCommentInput = () => {
  document.querySelector('.comment-input textarea')?.focus()
}

onMounted(fetchPost)
</script>

<style scoped>
.post-detail-page {
  background: linear-gradient(135deg, #f5f7fa 0%, #f3e5f5 100%);
  min-height: 100vh;
  padding: 20px 0 40px;
}

.post-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 32px 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(126, 87, 194, 0.1);
  border: 1px solid rgba(126, 87, 194, 0.1);
  position: relative;
  overflow: hidden;
}

.post-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #7e57c2, #b39ddb, #7e57c2);
  border-radius: 20px 20px 0 0;
}

/* 文章头部样式 */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f3e5f5;
  flex-wrap: wrap;
  gap: 20px;
}

.post-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #5e35b1;
  line-height: 1.4;
  margin: 0;
  padding: 0;
  border: none;
  flex: 1;
  min-width: 0; /* 防止标题过长挤压按钮 */
  word-break: break-word;
}

/* 文章操作按钮容器 */
.post-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

/* 编辑按钮样式 */
.edit-btn {
  padding: 6px 16px;
  height: 32px;
  font-size: 12px;
  border-radius: 6px;
  border: 1px solid #b3d8ff;
  background: linear-gradient(to right, #ecf5ff, #ecf5ff);
  color: #409eff;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
  min-width: 60px;
  justify-content: center;
  cursor: pointer;
  font-family: inherit;
  outline: none;
}

.edit-btn i {
  font-size: 12px;
  margin-right: 4px;
}

.edit-btn:hover {
  background: linear-gradient(to right, #d9ecff, #d9ecff);
  border-color: #409eff;
  color: #337ecc;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.15);
}

.edit-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
}

/* 文章删除按钮样式 */
.delete-post-btn {
  padding: 6px 16px;
  height: 32px;
  font-size: 12px;
  border-radius: 6px;
  border: 1px solid #ffcdd2;
  background: linear-gradient(to right, #fff, #fff);
  color: #f44336;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 4px rgba(244, 67, 54, 0.1);
  min-width: 60px;
  justify-content: center;
  cursor: pointer;
  font-family: inherit;
  outline: none;
}

.delete-post-btn i {
  font-size: 12px;
  margin-right: 4px;
}

.delete-post-btn:hover {
  background: linear-gradient(to right, #ffebee, #ffebee);
  border-color: #f44336;
  color: #d32f2f;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(244, 67, 54, 0.15);
}

.delete-post-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(244, 67, 54, 0.1);
}

.meta {
  display: flex;
  gap: 16px;
  align-items: center;
  color: #666;
  margin-bottom: 24px;
  flex-wrap: wrap;
  padding: 16px;
  background: #f9f5ff;
  border-radius: 12px;
  border: 1px solid #f3e5f5;
}

.meta .el-avatar {
  border: 2px solid #7e57c2;
  box-shadow: 0 2px 8px rgba(126, 87, 194, 0.2);
}

.author-name {
  font-weight: 600;
  color: #5e35b1;
  font-size: 15px;
}

.type-tag {
  background: #ede7f6;
  color: #7e57c2;
  border: none;
  font-weight: 500;
  border-radius: 12px;
  padding: 4px 12px;
}

.view-count {
  color: #9575cd;
  font-size: 14px;
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 封面图片容器 */
.cover-container {
  width: 100%;
  margin-bottom: 28px;
  border-radius: 16px;
  overflow: hidden;
  position: relative;
  background: #f9f5ff;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e1d5f7;
}

.cover-image {
  width: 100%;
  height: auto;
  max-height: 500px;
  object-fit: contain;
  border-radius: 16px;
  display: block;
}

/* 错误和加载状态 */
.image-error,
.image-loading {
  width: 100%;
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: #b39ddb;
  font-size: 16px;
  background: linear-gradient(135deg, #f3e5f5, #fff);
  border-radius: 16px;
  border: 2px dashed #d1c4e9;
}

.image-error i,
.image-loading i {
  font-size: 48px;
  color: #d1c4e9;
}

.image-loading i {
  animation: rotate 2s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.content {
  line-height: 1.8;
  margin-bottom: 40px;
  color: #444;
  font-size: 16px;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
  overflow-wrap: break-word;
  word-wrap: break-word;
}

.content :deep(p) {
  margin-bottom: 1.2em;
}

.content :deep(img) {
  max-width: 100% !important;
  height: auto !important;
  max-height: 500px;
  display: block;
  margin: 20px auto;
  border-radius: 12px;
  object-fit: contain;
  background: #f9f5ff;
  padding: 4px;
  box-shadow: 0 4px 12px rgba(126, 87, 194, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.content :deep(img:hover) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(126, 87, 194, 0.25);
}

.content :deep(a) {
  color: #7e57c2;
  text-decoration: none;
  border-bottom: 1px solid #d1c4e9;
  transition: all 0.3s;
}

.content :deep(a:hover) {
  color: #5e35b1;
  border-bottom-color: #7e57c2;
}

.comments-section {
  margin-top: 40px;
  padding-top: 28px;
  border-top: 2px solid #f3e5f5;
}

.comments-section h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #5e35b1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-input-wrapper {
  background: #f9f5ff;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 24px;
  border: 1px solid #ede7f6;
}

.comment-input :deep(.el-textarea__inner) {
  border: 2px solid #e1d5f7;
  border-radius: 12px;
  padding: 16px;
  font-size: 15px;
  transition: all 0.3s;
  background: #fff;
  color: #333;
}

.comment-input :deep(.el-textarea__inner:focus) {
  border-color: #7e57c2;
  box-shadow: 0 0 0 3px rgba(126, 87, 194, 0.1);
}

.comment-input :deep(.el-textarea__inner::placeholder) {
  color: #b39ddb;
}

.submit-btn {
  margin-top: 12px;
  background: linear-gradient(135deg, #7e57c2, #9575cd);
  border: none;
  border-radius: 10px;
  padding: 10px 24px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(126, 87, 194, 0.2);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(126, 87, 194, 0.3);
  background: linear-gradient(135deg, #6a1b9a, #7e57c2);
}

.submit-btn:disabled {
  opacity: 0.6;
  transform: none;
  box-shadow: none;
  cursor: not-allowed;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-card {
  border: none;
  border-radius: 16px;
  background: linear-gradient(to right, #fff, #fdfbff);
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
  padding: 20px;
}

.comment-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, #7e57c2, #d1c4e9);
  border-radius: 4px 0 0 4px;
}

.comment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(126, 87, 194, 0.15);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.comment-avatar {
  border: 2px solid #d1c4e9;
  transition: all 0.3s;
}

.comment-avatar:hover {
  transform: scale(1.1);
  border-color: #7e57c2;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-author {
  font-weight: 600;
  color: #5e35b1;
  font-size: 15px;
}

.time {
  color: #9575cd;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.time::before {
  content: '🕐';
  font-size: 10px;
}

/* 评论删除按钮样式 */
.custom-delete-btn {
  padding: 6px 16px;
  height: 32px;
  font-size: 12px;
  border-radius: 6px;
  border: 1px solid #ffcdd2;
  background: linear-gradient(to right, #fff, #fff);
  color: #f44336;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 4px rgba(244, 67, 54, 0.1);
  min-width: 60px;
  justify-content: center;
  cursor: pointer;
  font-family: inherit;
  outline: none;
}

.custom-delete-btn i {
  font-size: 12px;
  margin-right: 4px;
}

.custom-delete-btn:hover {
  background: linear-gradient(to right, #ffebee, #ffebee);
  border-color: #f44336;
  color: #d32f2f;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(244, 67, 54, 0.15);
}

.custom-delete-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(244, 67, 54, 0.1);
}

.comment-content {
  font-size: 15px;
  color: #333;
  line-height: 1.7;
  padding: 12px 16px;
  border-radius: 10px;
  margin-top: 8px;
  background: #fff;
  border: 1px solid #f3e5f5;
  position: relative;
  word-break: break-word;
}

.comment-content::before {
  content: '"';
  position: absolute;
  left: 8px;
  top: 4px;
  font-size: 24px;
  color: #d1c4e9;
  font-family: Georgia, serif;
}

.pagination {
  margin-top: 28px;
  display: flex;
  justify-content: center;
}

.pagination :deep(.el-pagination.is-background .btn-prev),
.pagination :deep(.el-pagination.is-background .btn-next),
.pagination :deep(.el-pagination.is-background .el-pager li) {
  border-radius: 10px;
  border: 1px solid #d1c4e9;
  background: #fff;
  color: #7e57c2;
  transition: all 0.3s;
}

.pagination :deep(.el-pagination.is-background .el-pager li:not(.disabled).active) {
  background: linear-gradient(135deg, #7e57c2, #9575cd);
  color: #fff;
  border: none;
  box-shadow: 0 4px 12px rgba(126, 87, 194, 0.2);
}

.pagination :deep(.el-pagination.is-background .el-pager li:not(.disabled):hover) {
  background: #f3e5f5;
  color: #5e35b1;
  transform: translateY(-2px);
}

.loading-skeleton {
  max-width: 800px;
  margin: 20px auto;
  padding: 32px 40px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(126, 87, 194, 0.1);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .post-container {
    margin: 12px;
    padding: 20px;
    border-radius: 16px;
  }
  
  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .post-header h1 {
    font-size: 22px;
    width: 100%;
  }
  
  .post-actions {
    align-self: flex-end;
  }
  
  .meta {
    padding: 12px;
    gap: 12px;
  }
  
  .cover-container {
    min-height: 150px;
  }
  
  .cover-image {
    max-height: 300px;
  }
  
  .content :deep(img) {
    max-height: 300px;
  }
  
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .custom-delete-btn {
    align-self: flex-end;
  }
  
  .comment-content {
    padding-left: 12px;
  }
  
  .comment-content::before {
    display: none;
  }
  
  .submit-btn {
    width: 100%;
  }
  
  .edit-btn,
  .delete-post-btn,
  .custom-delete-btn {
    padding: 4px 12px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .cover-container {
    min-height: 120px;
  }
  
  .cover-image {
    max-height: 250px;
  }
  
  .content :deep(img) {
    max-height: 250px;
  }
  
  .post-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>

<style>
/* 全局的删除确认对话框样式 */
.delete-confirm-dialog .el-message-box__header,
.delete-post-dialog .el-message-box__header {
  background: linear-gradient(135deg, #7e57c2, #9575cd);
  padding: 16px 20px;
  border-radius: 8px 8px 0 0;
}

.delete-confirm-dialog .el-message-box__title,
.delete-post-dialog .el-message-box__title {
  color: white;
  font-weight: 600;
}

.delete-confirm-dialog .el-message-box__content {
  padding: 20px;
  color: #5e35b1;
}

.delete-post-dialog .el-message-box__content {
  padding: 20px;
  color: #d32f2f;
  font-weight: 500;
}

.delete-confirm-dialog .el-message-box__btns,
.delete-post-dialog .el-message-box__btns {
  padding: 16px 20px 20px;
  border-top: 1px solid #f3e5f5;
}

.delete-confirm-dialog .el-button--danger,
.delete-post-dialog .el-button--danger {
  background: linear-gradient(135deg, #f44336, #ef5350);
  border: none;
  border-radius: 6px;
  padding: 8px 20px;
  transition: all 0.3s;
}

.delete-confirm-dialog .el-button--danger:hover,
.delete-post-dialog .el-button--danger:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}
</style>