import api from './index'  // axios 实例

/**
 * 上传文件，返回 URL
 * @param {File} file
 * @returns {Promise} res.data.url
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)

  return api.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
