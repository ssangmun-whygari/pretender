<template>
  <h1>로그인에 성공하였습니다.</h1>
</template>

<script setup>
  import { onMounted } from 'vue'
  import { useRouter } from 'vue-router';
  import { useNavigationStore } from '@/composables/stores/navigation'

  const navigationStore = useNavigationStore()
  const router = useRouter()
  const previousPage = navigationStore.getPreviousPage() || '/';

  onMounted(() => {
    router.push(previousPage)
  })
</script>