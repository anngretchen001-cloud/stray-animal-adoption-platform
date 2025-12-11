import api from './index' 

/**
 * 获取所有用户
 */
export function listUsers() {
  return api.get('/admin/users')
}

/**
 * 获取所有组织
 */
export function listOrganizations() {
  return api.get('/admin/organizations')
}

/**
 * 审核通过组织申请
 * @param {number} orgId 
 */
export function approveOrganization(orgId) {
  return api.put(`/admin/organizations/${orgId}/approve`)
}

/**
 * 审核拒绝组织申请
 * @param {number} orgId 
 */
export function rejectOrganization(orgId) {
  return api.put(`/admin/organizations/${orgId}/reject`)
}

/**
 * 封禁或启用用户
 * @param {number} userId 
 * @param {boolean} enabled 
 */
export function setUserEnabled(userId, enabled) {
  return api.put(`/admin/users/${userId}/enable`, null, {
    params: { enabled }
  })
}

/**
 * 封禁或启用组织
 * @param {number} orgId 
 * @param {boolean} enabled 
 */
export function setOrganizationEnabled(orgId, enabled) {
  return api.put(`/admin/organizations/${orgId}/enable`, null, {
    params: { enabled }
  })
}
