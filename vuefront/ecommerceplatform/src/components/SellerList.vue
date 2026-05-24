<template>
  <div class="seller-page">
    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-item">
          <input type="text" v-model="filters.id" placeholder="卖家ID" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.userName" placeholder="用户名" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.name" placeholder="真实姓名" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.phone" placeholder="手机号" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.storeName" placeholder="店铺名称" />
        </div>
      </div>
      <div class="filter-row" style="margin-top: 15px;">
        <div class="filter-item">
          <select v-model="filters.sex">
            <option value="">性别-全部</option>
            <option value="male">男</option>
            <option value="female">女</option>
          </select>
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.address" placeholder="地址" />
        </div>
        <div class="filter-item">
          <select v-model="filters.storeCategory">
            <option value="">店铺类型-全部</option>
            <option value="clothing">服饰</option>
            <option value="electronics">数码电子</option>
            <option value="food">食品</option>
            <option value="home_living">家居生活</option>
            <option value="sports">运动</option>
            <option value="mother_baby">母婴</option>
            <option value="books">图书</option>
            <option value="others">其他</option>
          </select>
        </div>
        <div class="filter-item">
          <select v-model="filters.status">
            <option value="">账号状态-全部</option>
            <option value="active">正常营业</option>
            <option value="locked">已关闭</option>
            <option value="deleted">已删除</option>
          </select>
        </div>
        <div class="filter-actions">
          <button class="btn-search" @click="searchSellers">搜索</button>
          <button class="btn-reset" @click="resetFilters">重置</button>
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-row">
      <div class="stat-item">
        <span class="stat-label">卖家总数：</span>
        <span class="stat-value">{{ sellerList.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">正常营业：</span>
        <span class="stat-value normal">{{ sellerList.filter(s => s.status === 'active').length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已关闭账号：</span>
        <span class="stat-value locked">{{ sellerList.filter(s => s.status === 'locked').length }}</span>
      </div>
    </div>

    <!-- 卖家列表 -->
    <div class="table-section">
      <table class="data-table">
        <thead>
          <tr>
            <th>卖家ID</th>
            <th>店铺头像</th>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>手机号</th>
            <th>店铺名称</th>
            <th>店铺类型</th>
            <th>账号状态</th>
            <th>入驻时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="seller in filteredSellerList" :key="seller.id">
            <td>{{ seller.id }}</td>
            <td>
              <div class="avatar">
                <img v-if="seller.image" :src="seller.image" alt="头像" class="avatar-img" />
                <span v-else class="avatar-icon">🏪</span>
              </div>
            </td>
            <td>{{ seller.userName }}</td>
            <td>{{ seller.name }}</td>
            <td>{{ formatPhone(seller.phone) }}</td>
            <td>{{ seller.storeName }}</td>
            <td>{{ getStoreCategoryText(seller.storeCategory) }}</td>
            <td>
              <span class="status-tag" :class="seller.status">
                {{ getStatusText(seller.status) }}
              </span>
            </td>
            <td>{{ seller.createTime }}</td>
            <td class="actions">
              <button class="btn-detail" @click="viewDetail(seller)">查看详情</button>
              <button
                v-if="seller.status === 'active'"
                class="btn-ban"
                @click="banSeller(seller.id)"
              >
                封禁账号
              </button>
              <button
                v-else-if="seller.status === 'locked'"
                class="btn-unban"
                @click="unbanSeller(seller.id)"
              >
                解封账号
              </button>
            </td>
          </tr>
          <tr v-if="filteredSellerList.length === 0">
            <td colspan="10" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>卖家详情</h3>
          <button class="modal-close" @click="showDetailModal = false">×</button>
        </div>
        <div v-if="currentSeller" class="modal-body">
          <div class="detail-row">
            <span class="detail-label">卖家ID</span>
            <span class="detail-value">{{ currentSeller.id }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">用户名</span>
            <span class="detail-value">{{ currentSeller.userName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">真实姓名</span>
            <span class="detail-value">{{ currentSeller.name }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">身份证号</span>
            <span class="detail-value">{{ formatIdNumber(currentSeller.idNumber) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">手机号</span>
            <span class="detail-value">{{ currentSeller.phone }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">性别</span>
            <span class="detail-value">{{ currentSeller.sex === 'male' ? '男' : '女' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">地址</span>
            <span class="detail-value">{{ currentSeller.address }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">店铺名称</span>
            <span class="detail-value">{{ currentSeller.storeName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">店铺类型</span>
            <span class="detail-value">{{ getStoreCategoryText(currentSeller.storeCategory) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">账号状态</span>
            <span class="detail-value status-tag" :class="currentSeller.status">
              {{ getStatusText(currentSeller.status) }}
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">入驻时间</span>
            <span class="detail-value">{{ currentSeller.createTime }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">最后修改时间</span>
            <span class="detail-value">{{ currentSeller.updateTime }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import axios from '../utils/axios'

const filters = reactive({
  id: '',
  userName: '',
  name: '',
  idNumber: '',
  phone: '',
  sex: '',
  address: '',
  storeName: '',
  storeCategory: '',
  status: ''
})

const sellerList = ref([])
const showDetailModal = ref(false)
const currentSeller = ref(null)

const filteredSellerList = computed(() => {
  return sellerList.value.filter(seller => {
    if (filters.id && seller.id !== parseInt(filters.id)) return false
    if (filters.userName && !seller.userName.toLowerCase().includes(filters.userName.toLowerCase())) return false
    if (filters.name && !seller.name.toLowerCase().includes(filters.name.toLowerCase())) return false
    if (filters.idNumber && !seller.idNumber.includes(filters.idNumber)) return false
    if (filters.phone && !seller.phone.includes(filters.phone)) return false
    if (filters.sex && seller.sex !== filters.sex) return false
    if (filters.address && !seller.address.toLowerCase().includes(filters.address.toLowerCase())) return false
    if (filters.storeName && !seller.storeName.toLowerCase().includes(filters.storeName.toLowerCase())) return false
    if (filters.storeCategory && seller.storeCategory !== filters.storeCategory) return false
    if (filters.status && seller.status !== filters.status) return false
    return true
  })
})

const fetchSellerList = async () => {
  try {
    const response = await axios.get('/admin/seller/list')
    if (response.code === 200) {
      sellerList.value = response.data || []
    } else {
      sellerList.value = []
    }
  } catch (error) {
    console.error('获取卖家列表失败:', error)
    sellerList.value = []
  }
}

const searchSellers = async () => {
  try {
    const params = {}
    if (filters.id) params.id = filters.id
    if (filters.userName) params.userName = filters.userName
    if (filters.name) params.name = filters.name
    if (filters.idNumber) params.idNumber = filters.idNumber
    if (filters.phone) params.phone = filters.phone
    if (filters.sex) params.sex = filters.sex
    if (filters.address) params.address = filters.address
    if (filters.storeName) params.storeName = filters.storeName
    if (filters.storeCategory) params.storeCategory = filters.storeCategory
    if (filters.status) params.status = filters.status

    const response = await axios.get('/admin/seller/query', { params })
    if (response.code === 200) {
      sellerList.value = response.data || []
    } else {
      alert(response.msg || '获取卖家列表失败')
    }
  } catch (error) {
    console.error('搜索卖家失败:', error)
    sellerList.value = []
  }
}

const resetFilters = () => {
  filters.id = ''
  filters.userName = ''
  filters.name = ''
  filters.idNumber = ''
  filters.phone = ''
  filters.sex = ''
  filters.address = ''
  filters.storeName = ''
  filters.storeCategory = ''
  filters.status = ''
  fetchSellerList()
}

const viewDetail = async (seller) => {
  try {
    const response = await axios.get(`/admin/seller/${seller.id}`)
    if (response.code === 200) {
      currentSeller.value = response.data
    } else {
      currentSeller.value = seller
    }
    showDetailModal.value = true
  } catch (error) {
    console.error('获取卖家详情失败:', error)
    currentSeller.value = seller
    showDetailModal.value = true
  }
}

const banSeller = async (id) => {
  if (confirm('确定要封禁该卖家账号吗？封禁后其名下商品将自动下架。')) {
    try {
      const response = await axios.put(`/admin/seller/${id}/ban`)
      if (response.code === 200) {
        alert('封禁成功')
        fetchSellerList()
      } else {
        alert(response.msg || '封禁失败')
      }
    } catch (error) {
      console.error('封禁卖家失败:', error)
      alert(error.response?.data?.msg || '封禁失败')
    }
  }
}

const unbanSeller = async (id) => {
  if (confirm('确定要解封该卖家账号吗？解封后商品需卖家重新申请上架。')) {
    try {
      const response = await axios.put(`/admin/seller/${id}/unban`)
      if (response.code === 200) {
        alert('解封成功')
        fetchSellerList()
      } else {
        alert(response.msg || '解封失败')
      }
    } catch (error) {
      console.error('解封卖家失败:', error)
      alert(error.response?.data?.msg || '解封失败')
    }
  }
}

const formatPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const formatIdNumber = (idNumber) => {
  if (!idNumber) return ''
  return idNumber.replace(/(\d{4})\d{8}(\d{4})/, '$1********$2')
}

const getStatusText = (status) => {
  const map = { active: '正常营业', locked: '已关闭', deleted: '已删除' }
  return map[status] || status
}

const getStoreCategoryText = (category) => {
  const map = {
    clothing: '服饰',
    electronics: '数码电子',
    food: '食品',
    home_living: '家居生活',
    sports: '运动',
    mother_baby: '母婴',
    books: '图书',
    others: '其他'
  }
  return map[category] || category
}

fetchSellerList()
</script>

<style scoped>
.seller-page {
  padding: 20px;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 15px;
}

.filter-row {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-item input,
.filter-item select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  min-width: 150px;
}

.filter-item input::placeholder {
  color: #999;
}

.filter-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.btn-search {
  background: #1a237e;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-reset {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.stats-row {
  display: flex;
  gap: 30px;
  margin-bottom: 15px;
  padding: 12px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stat-value {
  font-weight: 600;
  color: #333;
}

.stat-value.normal {
  color: #2e7d32;
}

.stat-value.locked {
  color: #c62828;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.data-table tbody tr:hover {
  background: #f8f9fa;
}

.empty-row {
  text-align: center;
  color: #999;
  padding: 40px !important;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #eee;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-icon {
  font-size: 18px;
}

.status-tag {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.status-tag.active {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-tag.locked {
  background: #ffebee;
  color: #c62828;
}

.status-tag.deleted {
  background: #f5f5f5;
  color: #999;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-detail,
.btn-ban,
.btn-unban {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.btn-detail {
  background: #409eff;
  color: white;
}

.btn-ban {
  background: #f56c6c;
  color: white;
}

.btn-unban {
  background: #67c23a;
  color: white;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 450px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
}

.modal-close {
  font-size: 24px;
  border: none;
  background: none;
  cursor: pointer;
  color: #999;
}

.modal-body {
  padding: 20px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-label {
  color: #666;
}

.detail-value {
  font-weight: 500;
}
</style>