import api from './index'

/**
 * 分页获取某篇文章的评论
 * @param {Object} params
 * @param {number} params.postId 文章ID
 * @param {number} params.pageNum 页码
 * @param {number} params.pageSize 每页数量
 */
export function pageComments({ postId, pageNum = 1, pageSize = 10 }) {
  return api
    .get('/comments/page', {
      params: { postId, pageNum, pageSize }
    })
    .then(res => res.data.data) // 直接返回分页数据
}

/**
 * 发布评论
 * @param {Object} data
 * @param {number} data.postId 文章ID
 * @param {string} data.content 评论内容
 */
export function createComment(data) {
  return api.post('/comments', data).then(res => res.data)
}
/**
 * 删除评论
 * @param {number} id 评论ID
 */
export function deleteComment(id) {
  return api.delete(`/comments/${id}`).then(res => res.data)
}