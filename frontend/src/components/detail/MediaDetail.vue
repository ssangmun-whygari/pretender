<template>
  <MediaDetailBackdop v-bind:mediaInfo="mediaInfo"/>
  <v-container fluid>
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <section v-if="hasContentProvider()">
          <h2>다음과 같은 플랫폼에서 볼 수 있어요</h2>
          <div class="d-flex align-center ga-3" style="height: 70px;">
            <img
              v-for="( _ , i) in getContentProviders"
              :src="getProviderLogoPath(i)"
            ><img>
            <!-- <img
              src="http://image.tmdb.org/t/p/w154/wwemzKWzjKYJFfCeiB57q3r4Bcm.png"
            ></img> -->
          </div>
        </section>
        <section>
          <h2>의견을 공유해보세요</h2>
          <v-sheet class="ma-3 pa-3 bg-light-blue-lighten-5" rounded>
            <div class="pb-3 d-flex align-center">
              <p>별점 : </p>
              <v-rating
                active-color="amber-accent-4"
                color="amber-accent-4"
                density="compact"
                size="x-large"
                half-increments
              />
            </div>
            <v-textarea 
              variant="outlined"
              bg-color="yellow-lighten-5"
              color="amber-accent-1"
            ></v-textarea>
            <div class="d-flex justify-end">
              <v-btn color="primary">게시하기</v-btn>
            </div>
          </v-sheet>
        </section>
        <Comments/>
      </v-col>
    </v-row>
  </v-container>

</template>


<script setup>
  // NOTE : v-img와 flexbox 같이 쓰지 말기
  // <div class="d-flex">
  //   <v-img 
  //     v-for="( _ , i) in getContentProviders"
  //     :src="getProviderLogoPath(i)"
  //   ></v-img>
  //   <v-img 
  //     src="http://image.tmdb.org/t/p/w154/wwemzKWzjKYJFfCeiB57q3r4Bcm.png"
  //   ></v-img>
  // </div>

  import { useRoute } from 'vue-router';
  import { ref, reactive, onMounted, computed } from 'vue'
  import axios from 'axios'
  import MediaDetailBackdop from './MediaDetailBackdop.vue';
  const id = useRoute().query.id // 주소창에서 id 쿼리를 얻어옴

  let mediaInfo = ref({})
  let contentProviderImageBaseUrl = "http://image.tmdb.org/t/p/w154"
  let hasWatched = ref(false)
  let videoPlaying = ref(false)

  // axios 요청
  async function getResponse() {
    let response = await axios.get(
      'http://localhost:8080/api/detail',
      {
        // TODO : 바꿔야 함
        params : {
          type: "tv",
          mediaId: id,
        }
      }
    ) // axios.get end
    mediaInfo.value = response.data
    console.log("============getResponse...")
    console.log(mediaInfo)
    console.log("============getResponse end")
  }
  getResponse()

  console.log("=================mediaInfo=================")
  console.log(mediaInfo)
  console.log("=================mediaInfo=================")

  let logRef = () => {
    console.log(contentProviderImageElements.value)
  }

  let getContentProviders = computed(() => {
    return mediaInfo.value.networks ? mediaInfo.value.networks : []
  })

  let hasContentProvider = () => {
    return getContentProviders.value.length > 0
  }

  let getProviderLogoPath = (index) => {
    return contentProviderImageBaseUrl + getContentProviders.value[index]["logo_path"]
  }
</script>