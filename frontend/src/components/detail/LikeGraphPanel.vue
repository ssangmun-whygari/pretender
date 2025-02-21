<template>
  여기다 그래프 그리기
  <!-- to peachea27 : 개발 완료시 삭제할 것-->
  <div>{{ `media_id : ${props.media_id}` }}</div>
  <div>{{ `media_type : ${props.media_type}` }}</div>
  <div>{{ `cast_info : ${JSON.stringify(props.cast_info)}` }}</div>
  <!-- to peachea27 : 개발 완료시 삭제할 것-->
</template>

<script setup>
  const props = defineProps({
    media_id: String,
    media_type: String,
    cast_info: Array,
  });
</script>