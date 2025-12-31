import api from './index'

/**
 * 分页查询文章
 * @param {Object} params
 * @param {number} params.pageNum
 * @param {number} params.pageSize
 * @param {string} params.keyword  标题关键字（可选）
 * @param {string} params.type     分类类型（可选）
 * @param {long} params.userId   用户ID（可选，查询某用户的文章）
 */
export function pagePosts({
  pageNum = 1,
  pageSize = 10,
  keyword = '',
  type = '',
  userId = null 
} = {}) {
  return api.get('/posts/page', {
    params: {
      pageNum,
      pageSize,
      keyword,
      type,
        userId
    }
  }).then(res => res.data.data) // 取data.data
}

/**
 * 获取文章详情
 */
export function getPost(id) {
  return api.get(`/posts/${id}`).then(res => res.data.data)
}

/**
 * 发布文章
 */
export function createPost(data) {
  return api.post('/posts', data).then(res => res.data)
}

/**
 * 更新文章
 */
export function updatePost(id, data) {
  return api.put(`/posts/${id}`, data).then(res => res.data)
}

/**
 * 删除文章
 */
export function deletePost(id) {
  return api.delete(`/posts/${id}`).then(res => res.data)
}
