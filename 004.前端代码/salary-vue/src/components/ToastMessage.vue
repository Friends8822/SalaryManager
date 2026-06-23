<template>
  <Teleport to="body">
    <div v-if="store.toastVisible" :class="['toast-pop', store.toastType]">
      <span>{{ store.toastType === 'success' ? '✓' : '✕' }}</span>
      {{ store.toastMsg }}
    </div>
  </Teleport>
</template>

<script setup>
import { useAppStore } from '../stores/app'
const store = useAppStore()
</script>

<style scoped>
.toast-pop {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
  padding: 16px 28px;
  border-radius: 16px;
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
  animation: toastIn 0.3s ease;
  pointer-events: none;
}
.toast-pop.success { background: linear-gradient(135deg, #7EC850, #4A9E3F); }
.toast-pop.error { background: linear-gradient(135deg, #FF8B94, #e05560); }
@keyframes toastIn {
  from { opacity: 0; transform: translate(-50%, -50%) scale(0.85); }
  to { opacity: 1; transform: translate(-50%, -50%) scale(1); }
}
</style>
