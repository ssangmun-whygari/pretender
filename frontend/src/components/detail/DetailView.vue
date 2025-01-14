<template>
  <AppHeader></AppHeader>
  <MediaDetail/>
</template>

<script setup>
import AppHeader from '../AppHeader.vue';
import { useNavigationStore } from '@/composables/stores/navigation';

// 방문할 때마다 previousPage 초기화
let navigationStore = useNavigationStore()
navigationStore.setPreviousPage('/')
</script>