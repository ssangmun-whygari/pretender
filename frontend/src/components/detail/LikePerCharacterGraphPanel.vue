<template>
  <h2>캐릭터 통계</h2>
  <!-- to peachea27 : 개발 완료시 삭제할 것-->
  <div>{{ `media_id : ${props.media_id}` }}</div>
  <div>{{ `media_type : ${props.media_type}` }}</div>
  <div>{{ `character_id : ${props.character_id}` }}</div>
  <!-- to peachea27 : 개발 완료시 삭제할 것-->
</template>

<script setup>
  const props = defineProps({
    media_id: String,
    media_type: String,
    character_id: Number,
  });
</script>