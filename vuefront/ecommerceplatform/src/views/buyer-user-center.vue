<template>
  <div class="buyer-container">
    <div class="page-header">
      <el-button @click="$router.push('/buyer/home')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="buyer-title">个人中心</h2>
    </div>
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <el-avatar :size="80" :src="userInfo.image || defaultAvatar" class="avatar" />
      <div class="user-details">
        <div class="username">{{ userInfo.userName }}</div>
        <div class="realname">{{ userInfo.name || '未设置真实姓名' }}</div>
        <div class="phone">{{ userInfo.phone || '未绑定手机号' }}</div>
      </div>
      <el-button type="primary" plain round @click="goToEdit">编辑资料</el-button>
    </div>

    <!-- 功能菜单网格 -->
    <div class="menu-grid">
      <div class="menu-item" @click="$router.push('/buyer/order/list')">
        <el-icon><List /></el-icon>
        <span>我的订单</span>
      </div>
      <div class="menu-item" @click="$router.push('/buyer/address')">
        <el-icon><Location /></el-icon>
        <span>收货地址</span>
      </div>
      <!-- 以下功能根据文档暂未实现，可隐藏或后续扩展 -->
      <div class="menu-item" @click="$router.push('/buyer/collect')" v-if="false">
        <el-icon><Star /></el-icon>
        <span>商品收藏</span>
      </div>
      <div class="menu-item" @click="$router.push('/buyer/favorite')" v-if="false">
        <el-icon><Shop /></el-icon>
        <span>店铺收藏</span>
      </div>
      <div class="menu-item" @click="$router.push('/buyer/message')" v-if="false">
        <el-icon><Message /></el-icon>
        <span>消息通知</span>
      </div>
      <div class="menu-item" @click="logout">
        <el-icon><SwitchButton /></el-icon>
        <span>退出登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, List, Location, Star, Shop, Message, SwitchButton } from '@element-plus/icons-vue'
import { userApi } from '@/api/buyer-user'
import defaultAvatar from '@/assets/default-avatar.png' // 请准备一张默认头像

const router = useRouter()
const userInfo = ref({})

const loadUserInfo = () => {
  const info = userApi.getLocalUserInfo()
  if (info) {
    userInfo.value = info
  } else {
    // 如果没有本地信息，可能是未登录或数据丢失，跳转登录
    router.push('/buyer/login')
  }
}

const goToEdit = () => {
  router.push('/buyer/user/info')
}

const logout = async () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('buyer_token')
    userApi.clearUserInfo()
    ElMessage.success('已退出登录')
    router.push('/buyer/login')
  }).catch(() => {})
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.buyer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}
.user-card {
  display: flex;
  align-items: center;
  gap: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #fff 100%);
  border-radius: 16px;
  padding: 24px 32px;
  margin-bottom: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.user-details {
  flex: 1;
}
.username {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
}
.realname, .phone {
  color: #7f8c8d;
  margin-top: 6px;
  font-size: 14px;
}
.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 20px;
}
.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 24px 12px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  color: #4a5568;
}
.menu-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  color: #409eff;
}
.menu-item .el-icon {
  font-size: 32px;
}
.menu-item span {
  font-size: 14px;
}
</style>