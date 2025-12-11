import api from './index'

// 注册
export const register = (data) => {
  return api.post('/users/register', data)
}
// 救助组织注册
export const registerOrganization = (data) => {
  return api.post('/organizations/register', data)
}
// 登录
export const login = (data) => {
  return api.post('/users/login', data)
}

// 修改密码
export const updatePassword = (data) => {
  return api.put('/users/password', data)
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return api.get('/users/me')
}

// 更新资料
export const updateProfile = (data) => {
  return api.put('/users/profile', data)
}
