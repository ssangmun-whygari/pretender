<template>
    <h1>just Test</h1>
    <div>
      <v-row class="px-3">
          <v-col cols="6" class="ga-3">
              <v-card elevation="10">
                <v-img 
                  src="http://image.tmdb.org/t/p/w500/ukVVnY9ovwl78WE5KndcpA6SnAm.jpg"
                  width="200px"
                  class="mx-auto"
                  >
                </v-img>
              </v-card>
          </v-col>
          <v-col cols="6" class="ga-3">
              <v-card>
                <v-img 
                  src="https://cdn.vuetifyjs.com/images/cards/house.jpg" 
                  width="px"
                  class="mx-auto"
                  >
                </v-img> 
              </v-card>
          </v-col>
      </v-row>
    </div>
    <div><v-btn v-on:click="requestAuthExp">인증 요청(Basic 헤더, 실험용)</v-btn></div>
    <div><v-btn v-on:click="requestAuthExp2">인증 요청(세션, 실험용)</v-btn></div>
    <div><v-btn v-on:click="authenticatedExp">로그인되었는가?</v-btn></div>
</template>

<script setup>
  import { useRouter } from 'vue-router';
  import { useDisplay } from 'vuetify';
  import axios from 'axios'
  import { ref, onMounted } from 'vue'
  const { lgAndUp } = useDisplay();
  const router = useRouter()
    
  async function requestAuthExp() {
    console.log("requestAuthExp...")
    let response = await axios.get(
      'http://localhost:8080/api/myPage',
      {
        auth: {
          username: 'whygari4321',
          password: 'abc1234'
        },

        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    console.log(response)
  }

  async function requestAuthExp2() {
    console.log("requestAuthExp2...")
    let response = await axios.get(
      'http://localhost:8080/api/myPage',
      {
        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    console.log(response)
  }

  async function authenticatedExp() {
    let response = await axios.get(
      'http://localhost:8080/api/authenticated',
      {
        withCredentials: true,

        headers: {
          "X-Requested-With": "XMLHttpRequest"
        }
      }
    ) // axios.get end
    console.log("response : " + response)
    console.log(response)
  }
</script>