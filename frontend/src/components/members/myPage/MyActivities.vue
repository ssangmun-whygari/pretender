<template>
  <v-row justify="center">
    <v-col lg="8" cols="12">
      <h1 class="mt-3 mb-3">내가 남긴 리뷰/댓글</h1>
      <v-text-field
        class="w-100"
        variant="outlined"
        append-inner-icon="mdi-magnify"
        density="compact"
        placeholder="리뷰/댓글을 남긴 작품 제목을 입력하세요..."
        v-on:keyup.enter="onEnter"
        v-model="word"
      ></v-text-field>
      <v-list lines="two">
        <a href="http://localhost:3000/detail?id=120550&type=tv">
        <v-list-item
          :title="'와 입이 안다물어진다'"
          :subtitle="'극한직업 / 2025-01-12 작성'"
          @click=""
        ></v-list-item>
        </a>
          <v-divider class="border-opacity-50"></v-divider>
        <a href="http://localhost:3000/detail?id=93405&type=tv">
          <v-list-item
            :title="'크 완벽하다 진짜 다 알고봐도 눈물이 찔끔나온다'"
            :subtitle="'오징어게임 / 2024-11-22 작성'"
            @click=""
          ></v-list-item>
        </a>
          <v-divider class="border-opacity-50"></v-divider>
      </v-list>
    </v-col>
  </v-row>
</template>

<style>
  .v-input__details {
    display: none;
  }
</style>

<script setup>
  const onEnter = async () => {
    // 검색창에서 엔터를 눌렀을 때 로직...
  }
</script>