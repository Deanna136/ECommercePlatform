<template>
  <el-container class="seller-layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <img src="@/assets/logo.png" alt="logo" />
        </div>
        <span class="logo-text">卖家后台</span>
      </div>
      <el-menu
        :default-active="$route.path"
        class="sidebar-menu"
        router
        mode="vertical"
      >
        <el-menu-item index="/seller/product/list">
          <el-icon class="menu-icon"><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/seller/order/list">
          <el-icon class="menu-icon"><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/seller/profile">
          <el-icon class="menu-icon"><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
      <div class="logout-btn">
        <el-button type="danger" @click="handleLogout" block>
          <el-icon><LogOut /></el-icon>
          退出登录
        </el-button>
      </div>
    </el-aside>
    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left">
          <span class="store-label">我的店铺</span>
          <span class="store-name">{{ sellerUserStore.storeName }}</span>
        </div>
        <div class="header-right">
          <div class="user-avatar">
            <User />
          </div>
          <span class="user-name">{{ sellerUserStore.userName }}</span>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Goods, Document, User, LogOut } from '@element-plus/icons-vue'
import { useSellerUserStore } from '@/stores/sellerUserStore'

const router = useRouter()
const sellerUserStore = useSellerUserStore()

const handleLogout = () => {
  sellerUserStore.sellerLogout()
  ElMessage.success('退出登录成功')
  router.push('/seller/login')
}
</script>

<style scoped>
.seller-layout {
  height: 100vh;
  background: var(--bg-color);
}

.sidebar {
  background: var(--sidebar-bg);
  color: #fff;
  position: relative;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.15);
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 16px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #409eff 0%, #3b82f6 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  padding: 4px;
}

.logo-icon img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}

.sidebar-menu {
  border-right: none;
}

.sidebar-menu .el-menu-item {
  color: #b8c4d4;
  margin: 6px 12px;
  border-radius: 10px;
  padding: 12px 16px;
  transition: all var(--transition-normal);
}

.sidebar-menu .el-menu-item:hover {
  background: var(--sidebar-hover);
  color: #fff;
  transform: translateX(4px);
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, var(--primary-color) 0%, #3b82f6 100%);
  color: #fff;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
}

.menu-icon {
  font-size: 16px;
}

.logout-btn {
  position: absolute;
  bottom: 24px;
  width: calc(100% - 48px);
  left: 24px;
}

.logout-btn .el-button {
  background: rgba(245, 108, 108, 0.15);
  border: 1px solid rgba(245, 108, 108, 0.3);
  color: #f56c6c;
  transition: all var(--transition-normal);
}

.logout-btn .el-button:hover {
  background: rgba(245, 108, 108, 0.25);
  transform: translateY(-1px);
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.header {
  background: linear-gradient(135deg, #ffffff 0%, #fafbfc 100%);
  border-bottom: 1px solid var(--border-color-light);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 68px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
}

.store-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-right: 8px;
}

.store-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #6366f1;
  overflow: hidden;
  margin-right: 12px;
}

.user-avatar .avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 14px;
  color: var(--text-regular);
}

.main-content {
  padding: 24px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-normal);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
