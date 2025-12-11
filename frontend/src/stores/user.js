import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    role: localStorage.getItem('role') || '',
    username: localStorage.getItem('username') || '',
    avatar: localStorage.getItem('avatar') || ''  // 新增
  }),
  actions: {
    setToken(t) {
      this.token = t
      localStorage.setItem('token', t)
    },
    setRole(r) {
      this.role = r
      localStorage.setItem('role', r)
    },
    setUsername(name) {
      this.username = name
      localStorage.setItem('username', name)
    },
    setAvatar(url) {   // 新增
      this.avatar = url
      localStorage.setItem('avatar', url)
    },
    logout() {
      this.token = ''
      this.role = ''
      this.username = ''
      this.avatar = ''
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('username')
      localStorage.removeItem('avatar')
    }
  }
})
