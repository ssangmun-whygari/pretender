<template>
    <!-- flex와 grid 함께 사용하지 말기 (어째선지 제대로 정렬이 안됨)-->
    <v-container class="d-flex flex-column justify-center" style="height:100%">
        <h1 style="text-align: center;" class="mb-5">pretender</h1>
        <div>
          <v-text-field
            class="mx-auto main-search-bar"
            :max-width="lgAndUp ? 1000 : null"
            placeholder="제목을 검색하세요"
            v-on:keyup.enter="onEnter"
            v-model="word"
          ></v-text-field>
        </div>
    </v-container>
</template>

<script setup>
  import { useRouter } from 'vue-router';
  import { useDisplay } from 'vuetify';
  import axios from 'axios'
  import { ref } from 'vue'
  const { lgAndUp } = useDisplay();
  const router = useRouter()

  let word = ref('')
  let responseData = ref(null)

  // axios 요청
  async function onEnter() {
      router.push({
            path: '/search',
            query: {
              word: word.value
            }
          })
    }
</script>