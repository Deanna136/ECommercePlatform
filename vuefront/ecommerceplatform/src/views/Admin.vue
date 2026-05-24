<template>
  <div class="admin-wrapper">
    <!-- 侧边栏 -->
    <Sidebar 
      :current-menu="currentMenu"
      :current-sub-menu="currentSubMenu"
      @menu-change="handleMenuChange"
      @submenu-change="handleSubMenuChange"
    />
    
    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 顶部导航 -->
      <div class="top-header">
        <div class="header-left">
          <span class="page-title">{{ currentPageTitle }}</span>
        </div>
        <div class="header-right">
          <div class="user-info" @click="toggleUserMenu">
            <span class="user-name">{{ adminInfo.userName || '管理员' }}</span>
            <span class="dropdown-arrow">▼</span>
          </div>
          
          <!-- 用户下拉菜单 -->
          <div v-if="showUserMenu" class="user-dropdown">
            <div class="dropdown-item" @click="openChangePasswordModal">
              <span class="dropdown-icon">🔑</span>
              <span>修改密码</span>
            </div>
            <div class="dropdown-item logout" @click="logout">
              <span class="dropdown-icon">🚪</span>
              <span>退出登录</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 内容区域 -->
      <div class="content-area">
        <BuyerList v-if="currentMenu === 'buyer'" />
        <SellerList v-else-if="currentMenu === 'seller'" />
        <ProductList v-else-if="currentMenu === 'product'" />
        <OrderList v-else-if="currentMenu === 'order'" />
        <div v-else class="empty-page">
          <div class="empty-icon">📄</div>
          <div class="empty-text">暂无内容</div>
        </div>
      </div>
    </div>
    
    <!-- 修改密码弹窗 -->
    <div v-if="showPasswordModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="modal-close" @click="closePasswordModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>管理员ID</label>
            <input type="text" :value="adminInfo.id" disabled />
          </div>
          <div class="form-item">
            <label>新密码 <span class="required">*</span></label>
            <input 
              type="password" 
              v-model="passwordForm.newPassword" 
              placeholder="请输入新密码（≥6位）"
            />
          </div>
          <div class="form-item">
            <label>确认密码 <span class="required">*</span></label>
            <input 
              type="password" 
              v-model="passwordForm.confirmPassword" 
              placeholder="请再次输入新密码"
            />
          </div>
          <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closePasswordModal">取消</button>
          <button class="btn-confirm" @click="submitChangePassword" :disabled="loading">
            <span v-if="loading">修改中...</span>
            <span v-else>确认修改</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from '../components/Sidebar.vue'
import BuyerList from '../components/BuyerList.vue'
import SellerList from '../components/SellerList.vue'
import ProductList from '../components/ProductList.vue'
import OrderList from '../components/OrderList.vue'
import axios from '../utils/axios'

const router = useRouter()

const currentMenu = ref('buyer')
const currentSubMenu = ref('')
const showUserMenu = ref(false)
const showPasswordModal = ref(false)
const loading = ref(false)
const passwordError = ref('')

const adminInfo = ref({
  id: 1,
  userName: 'admin'
})

const passwordForm = ref({
  newPassword: '',
  confirmPassword: ''
})

const pageTitles = {
  home: '首页',
  buyer: '买家列表',
  seller: '卖家列表',
  product: '商品列表',
  order: '订单列表',
  data: '销售情况统计',
  logistics: '物流管理',
  help: '帮助中心'
}

const currentPageTitle = computed(() => {
  return pageTitles[currentMenu.value] || '首页'
})

const handleMenuChange = (menuId) => {
  currentMenu.value = menuId
}

const handleSubMenuChange = (subMenuId) => {
  currentSubMenu.value = subMenuId
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const openChangePasswordModal = () => {
  showUserMenu.value = false
  showPasswordModal.value = true
  passwordForm.value = {
    newPassword: '',
    confirmPassword: ''
  }
  passwordError.value = ''
}

const closePasswordModal = () => {
  showPasswordModal.value = false
  passwordForm.value = {
    newPassword: '',
    confirmPassword: ''
  }
  passwordError.value = ''
}

const submitChangePassword = async () => {
  passwordError.value = ''
  
  if (!passwordForm.value.newPassword) {
    passwordError.value = '请输入新密码'
    return
  }
  
  if (passwordForm.value.newPassword.length < 6) {
    passwordError.value = '密码长度不能少于6位'
    return
  }
  
  if (!passwordForm.value.confirmPassword) {
    passwordError.value = '请确认密码'
    return
  }
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordError.value = '两次输入的密码不一致'
    return
  }
  
  loading.value = true
  
  try {
    const response = await axios.post('/admin/updatePwd', {
      id: adminInfo.value.id,
      password: passwordForm.value.newPassword
    })
    
    if (response.code === 200) {
      alert('密码修改成功，请重新登录')
      closePasswordModal()
      logout()
    } else {
      passwordError.value = response.msg || '修改密码失败'
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    passwordError.value = error.response?.data?.msg || '修改密码失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const logout = () => {
  showUserMenu.value = false
  localStorage.removeItem('admin')
  router.push('/login')
}

onMounted(() => {
  const admin = localStorage.getItem('admin')
  if (admin) {
    try {
      const adminObj = JSON.parse(admin)
      adminInfo.value = {
        id: adminObj.id,
        userName: adminObj.userName || '管理员'
      }
    } catch (e) {
      console.error('解析admin信息失败', e)
    }
  }
  
  document.addEventListener('click', (e) => {
    const userMenu = document.querySelector('.user-info')
    if (userMenu && !userMenu.contains(e.target)) {
      showUserMenu.value = false
    }
  })
})
</script>

<style scoped>
.admin-wrapper {
  display: flex;
  min-height: 100vh;
}

.main-content {
  flex: 1;
  margin-left: 200px;
  background-color: #f5f7fa;
}

.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: relative;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 15px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.user-phone {
  font-size: 12px;
  color: #999;
}

.dropdown-arrow {
  font-size: 10px;
  color: #999;
  margin-left: 5px;
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 150px;
  z-index: 1000;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px;
  color: #333;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.dropdown-item.logout {
  border-top: 1px solid #eee;
  color: #f56c6c;
}

.dropdown-icon {
  font-size: 16px;
}

.content-area {
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.empty-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #999;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 16px;
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
  z-index: 2000;
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
  font-size: 16px;
  color: #333;
}

.modal-close {
  font-size: 24px;
  border: none;
  background: none;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.form-item label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.required {
  color: #f56c6c;
}

.form-item input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-item input:focus {
  outline: none;
  border-color: #1a237e;
}

.form-item input:disabled {
  background-color: #f5f5f5;
  color: #999;
  cursor: not-allowed;
}

.error-message {
  color: #f56c6c;
  font-size: 13px;
  margin-top: -10px;
  margin-bottom: 15px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

.btn-cancel,
.btn-confirm {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-confirm {
  background: #1a237e;
  color: white;
}

.btn-confirm:hover:not(:disabled) {
  background: #283593;
}

.btn-confirm:disabled {
  background: #9fa8da;
  cursor: not-allowed;
}
</style>