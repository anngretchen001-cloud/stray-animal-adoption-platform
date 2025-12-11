// src/api/application.js
import api from './index'

/**
 * 提交领养申请
 * @param {Object} application - { animalId: number, message?: string }
 */
export function submitAdoption(application) {
  return api.post('/applications', application)
}

/**
 * 取消领养申请
 * @param {number} applicationId
 */
export function cancelAdoption(applicationId) {
  return api.post(`/applications/${applicationId}/cancel`)
}

/**
 * 获取当前用户的领养申请（分页）
 * @param {number} pageNum
 * @param {number} pageSize
 */
export function getMyApplications(pageNum = 1, pageSize = 10) {
  return api.get('/applications/my', {
    params: { pageNum, pageSize }
  })
}

export function getOrgApplications({ pageNum = 1, pageSize = 10, status } = {}) {
  return api.get('/applications/org', {
    params: { pageNum, pageSize, status }
  })
}

/**
 * 审核通过（组织）
 * @param {number} applicationId
 */
export function approveApplication(applicationId) {
  return api.post(`/applications/${applicationId}/approve`)
}

/**
 * 审核拒绝（组织）
 * @param {number} applicationId
 */
export function rejectApplication(applicationId) {
  return api.post(`/applications/${applicationId}/reject`)
}
