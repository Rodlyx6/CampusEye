import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
  withCredentials: true
})

export const register = (payload) => http.post('/auth/register', payload)
export const login = (payload) => http.post('/auth/login', payload)
export const currentUser = () => http.get('/auth/currentUser')
export const logout = () => http.post('/auth/logout')

export const getBuildingList = () => http.get('/building/list')
export const getBuildingDetail = (id) => http.get(`/building/detail/${id}`)
export const updateBuilding = (payload) => http.put('/building/update', payload)

export const getBuildingImages = (buildingId) => http.get(`/building-image/listByBuildingId/${buildingId}`)
export const replaceBuildingImages = (payload) => http.post('/building-image/replace', payload)
export const deleteBuildingImage = (id) => http.delete(`/building-image/delete/${id}`)

export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const uploadFilesBatch = (files) => {
  const formData = new FormData()
  Array.from(files || []).forEach((file) => formData.append('files', file))
  return http.post('/file/uploadBatch', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 评论相关
export const getComments = (buildingId) => http.get(`/comment/list/${buildingId}`)
export const addComment = (payload) => http.post('/comment/add', payload)
export const deleteComment = (id) => http.delete(`/comment/delete/${id}`)

// 收藏相关
export const toggleFavorite = (buildingId) => http.post(`/favorite/toggle/${buildingId}`)
export const getFavoriteStatus = (buildingId) => http.get(`/favorite/status/${buildingId}`)
export const getMyFavoriteIds = () => http.get('/favorite/my')

// 用户管理
export const getUserList = () => http.get('/user/list')
export const adminUpdateUser = (payload) => http.post('/user/admin/update', payload)
export const adminDeleteUser = (id) => http.delete(`/user/admin/delete/${id}`)
export const updateProfile = (payload) => http.post('/user/updateProfile', payload)
