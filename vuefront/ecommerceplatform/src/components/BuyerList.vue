<template>
  <div class="buyer-page">
    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-item">
          <input type="text" v-model="filters.id" placeholder="买家ID" />
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
          <select v-model="filters.sex">
            <option value="">性别-全部</option>
            <option value="male">男</option>
            <option value="female">女</option>
          </select>
        </div>
      </div>
      <div class="filter-row" style="margin-top: 15px;">
        <div class="filter-item">
          <input type="text" v-model="filters.idNumber" placeholder="身份证号" />
        </div>
        <div class="filter-item">
          <input type="text" v-model="filters.address" placeholder="地址" />
        </div>
        <div class="filter-item">
          <select v-model="filters.status">
            <option value="">账号状态-全部</option>
            <option value="active">正常</option>
            <option value="locked">禁用</option>
            <option value="deleted">已删除</option>
          </select>
        </div>
        <div class="filter-actions">
          <button class="btn-search" @click="searchBuyers">搜索</button>
          <button class="btn-reset" @click="resetFilters">重置</button>
        </div>
      </div>
    </div>

    <!-- 数据统计 -->
    <div class="stats-row">
      <div class="stat-item">
        <span class="stat-label">买家总数：</span>
        <span class="stat-value">{{ buyerList.length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">正常账号：</span>
        <span class="stat-value normal">{{ buyerList.filter(b => b.status === 'active').length }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已禁用账号：</span>
        <span class="stat-value locked">{{ buyerList.filter(b => b.status === 'locked').length }}</span>
      </div>
    </div>

    <!-- 买家列表 -->
    <div class="table-section">
      <table class="data-table">
        <thead>
          <tr>
            <th>买家ID</th>
            <th>买家头像</th>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>手机号</th>
            <th>性别</th>
            <th>地址</th>
            <th>账号状态</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="buyer in filteredBuyerList" :key="buyer.id">
            <td>{{ buyer.id }}</td>
            <td>
              <div class="avatar">
                <img v-if="buyer.image" :src="buyer.image" alt="头像" class="avatar-img" />
                <span v-else class="avatar-icon">👤</span>
              </div>
            </td>
            <td>{{ buyer.userName }}</td>
            <td>{{ buyer.name }}</td>
            <td>{{ formatPhone(buyer.phone) }}</td>
            <td>{{ buyer.sex === 'male' ? '男' : '女' }}</td>
            <td>{{ buyer.address }}</td>
            <td>
              <span class="status-tag" :class="buyer.status">
                {{ getStatusText(buyer.status) }}
              </span>
            </td>
            <td>{{ buyer.createTime }}</td>
            <td class="actions">
              <button class="btn-detail" @click="viewDetail(buyer)">查看详情</button>
              <button
                v-if="buyer.status === 'active'"
                class="btn-ban"
                @click="banBuyer(buyer.id)"
              >
                封禁账号
              </button>
              <button
                v-else-if="buyer.status === 'locked'"
                class="btn-unban"
                @click="unbanBuyer(buyer.id)"
              >
                解封账号
              </button>
            </td>
          </tr>
          <tr v-if="filteredBuyerList.length === 0">
            <td colspan="10" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetailModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>买家详情</h3>
          <button class="modal-close" @click="showDetailModal = false">×</button>
        </div>
        <div v-if="currentBuyer" class="modal-body">
          <div class="detail-row">
            <span class="detail-label">买家ID</span>
            <span class="detail-value">{{ currentBuyer.id }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">用户名</span>
            <span class="detail-value">{{ currentBuyer.userName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">真实姓名</span>
            <span class="detail-value">{{ currentBuyer.name }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">身份证号</span>
            <span class="detail-value">{{ formatIdNumber(currentBuyer.idNumber) }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">手机号</span>
            <span class="detail-value">{{ currentBuyer.phone }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">性别</span>
            <span class="detail-value">{{ currentBuyer.sex === 'male' ? '男' : '女' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">地址</span>
            <span class="detail-value">{{ currentBuyer.address }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">账号状态</span>
            <span class="detail-value status-tag" :class="currentBuyer.status">
              {{ getStatusText(currentBuyer.status) }}
            </span>
          </div>
          <div class="detail-row">
            <span class="detail-label">注册时间</span>
            <span class="detail-value">{{ currentBuyer.createTime }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">最后修改时间</span>
            <span class="detail-value">{{ currentBuyer.updateTime }}</span>
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
  status: ''
})

const buyerList = ref([])
const showDetailModal = ref(false)
const currentBuyer = ref(null)

const filteredBuyerList = computed(() => {
  return buyerList.value.filter(buyer => {
    if (filters.id && buyer.id !== parseInt(filters.id)) return false
    if (filters.userName && !(buyer.userName || '').toLowerCase().includes(filters.userName.toLowerCase())) return false
    if (filters.name && !(buyer.name || '').toLowerCase().includes(filters.name.toLowerCase())) return false
    if (filters.idNumber && !(buyer.idNumber || '').includes(filters.idNumber)) return false
    if (filters.phone && !(buyer.phone || '').includes(filters.phone)) return false
    if (filters.sex && buyer.sex !== filters.sex) return false
    if (filters.address && !(buyer.address || '').toLowerCase().includes(filters.address.toLowerCase())) return false
    if (filters.status && buyer.status !== filters.status) return false
    return true
  })
})

const fetchBuyerList = async () => {
  try {
    const response = await axios.get('/admin/buyer/list')
    if (response.code === 200) {
      buyerList.value = response.data || []
    } else {
      buyerList.value = []
    }
  } catch (error) {
    console.error('获取买家列表失败:', error)
    buyerList.value = []
  }
}

const searchBuyers = async () => {
  try {
    const params = {}
    if (filters.id) params.id = filters.id
    if (filters.userName) params.userName = filters.userName
    if (filters.name) params.name = filters.name
    if (filters.idNumber) params.idNumber = filters.idNumber
    if (filters.phone) params.phone = filters.phone
    if (filters.sex) params.sex = filters.sex
    if (filters.address) params.address = filters.address
    if (filters.status) params.status = filters.status

    const response = await axios.get('/admin/buyer/query', { params })
    if (response.code === 200) {
      buyerList.value = response.data || []
    } else {
      alert(response.msg || '获取买家列表失败')
    }
  } catch (error) {
    console.error('搜索买家失败:', error)
    buyerList.value = []
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
  filters.status = ''
  fetchBuyerList()
}

const viewDetail = async (buyer) => {
  try {
    const response = await axios.get(`/admin/buyer/${buyer.id}`)
    if (response.code === 200) {
      currentBuyer.value = response.data
    } else {
      currentBuyer.value = buyer
    }
    showDetailModal.value = true
  } catch (error) {
    console.error('获取买家详情失败:', error)
    currentBuyer.value = buyer
    showDetailModal.value = true
  }
}

const banBuyer = async (id) => {
  if (confirm('确定要封禁该买家账号吗？')) {
    try {
      const response = await axios.put(`/admin/buyer/${id}/ban`)
      if (response.code === 200) {
        alert('封禁成功')
        fetchBuyerList()
      } else {
        alert(response.msg || '封禁失败')
      }
    } catch (error) {
      console.error('封禁买家失败:', error)
      alert(error.response?.data?.msg || '封禁失败')
    }
  }
}

const unbanBuyer = async (id) => {
  if (confirm('确定要解封该买家账号吗？')) {
    try {
      const response = await axios.put(`/admin/buyer/${id}/unban`)
      if (response.code === 200) {
        alert('解封成功')
        fetchBuyerList()
      } else {
        alert(response.msg || '解封失败')
      }
    } catch (error) {
      console.error('解封买家失败:', error)
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
  const map = { active: '正常', locked: '禁用', deleted: '已删除' }
  return map[status] || status
}

fetchBuyerList()
</script>

<style scoped>
.buyer-page {
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