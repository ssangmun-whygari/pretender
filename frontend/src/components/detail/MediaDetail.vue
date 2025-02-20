<template>
  <MediaDetailBackdop v-bind:mediaInfo="mediaInfo"/>
  <v-container fluid id="container">
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <v-expand-transition class="contents">
          <div v-show="showCharacterView === false">
            <v-sheet v-if="hasContentProvider()" border class="mt-3 mb-3 pa-3 rounded-lg">
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
            </v-sheet>

            <v-sheet border class="mt-3 mb-3 pa-3 rounded-lg">
              <h2>이 작품에서 가장 인기있는 캐릭터는?</h2>
              <div class="d-flex justify-center">
                <div style="font-size: 120px; filter: grayscale(100%);">🤔</div>
              </div>
              <div class="d-flex justify-center align-center">
                <h3>아직 투표한 사람이 없어요</h3><span class="ml-3"> <a href="javascript:void(0);" @click="goToCharacterVote">👉투표하러 가기</a></span>
              </div>
            </v-sheet>

            <v-sheet border class="mt-3 mb-3 pa-3 rounded-lg">
              <h2>의견을 공유해보세요</h2>
              <v-sheet class="pa-3 bg-light-blue-lighten-5" rounded>
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
              <Comments/>
            </v-sheet>

          </div>
        </v-expand-transition>

        <v-expand-transition class="contents">
          <div v-show="showCharacterView === true">
            <CharacterVote v-bind:id="id" @hide-character-view="onHideCharacterView"/>
          </div>
        </v-expand-transition>

      </v-col>
    </v-row>
  </v-container>

</template>

<style scoped>
  #container {
    background-color: #F8FAFD;
    /* background-color: aqua; */
  }

  .contents {
    background-color: transparent;
  }

  a:link {
    color:gray;
    text-decoration:none;
  }
  a:visited {
    color:grey;
    text-decoration:none;
  }
</style>

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
  import CharacterVote from './CharacterVote.vue';
  
  let showCharacterView = ref(false)
  const id = ref(useRoute().query.id) // 주소창에서 id 쿼리를 얻어옴

  function goToCharacterVote() {
    showCharacterView.value = true
  }

  function onHideCharacterView() {
    showCharacterView.value = false
  }

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
          mediaId: id.value,
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