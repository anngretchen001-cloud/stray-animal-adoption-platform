import api from './index'  // 你的 axios 实例

// 分页查询动物（支持搜索和按年龄排序）
export function listAnimals({ pageNum = 1, pageSize = 10, type, status, search = '', sortByAge = '' }) {
  return api.get('/animals/list', { 
    params: { pageNum, pageSize, type, status, search, sortByAge } 
  })
}

// 分页查询当前组织的动物（组织后台）
export function listOrgAnimals({ pageNum = 1, pageSize = 10 } = {}) {
  return api.get('/animals/org', {
    params: { pageNum, pageSize }
  })
}


// 获取动物详情
export function getAnimal(id) {
  return api.get(`/animals/${id}`)
}

// 添加动物
export function addAnimal(data) {
  return api.post('/animals/add', data)
}

// 更新动物
export function updateAnimal(id, data) {
  return api.put(`/animals/${id}`, data)
}

// 删除动物
export function deleteAnimal(id) {
  return api.delete(`/animals/${id}`)
}
