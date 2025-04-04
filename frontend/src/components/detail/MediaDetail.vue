<template>
  <MediaDetailBackdop v-bind:mediaInfo="mediaInfo"/>
  <v-container fluid id="container">
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <v-expand-transition class="contents">
          <div v-show="showCharacterView === false">
            <v-sheet v-if="hasContentProvider()" border class="mt-3 mb-3 pa-3 rounded-lg">
              <h2>다음과 같은 플랫폼에서 볼 수 있어요</h2>
              <div class="d-flex align-center ga-3">
                <img
                  v-for="( _ , i) in getContentProviders"
                  :src="getProviderLogoPath(i)"
                ><img>
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
              <h2 class="mb-3">의견을 공유해보세요</h2>
              <v-sheet class="mb-3 pa-3" border>
                <div class="pb-3 d-flex align-center">
                  <p>별점 : </p>
                  <v-rating
                    active-color="amber-accent-4"
                    color="amber-accent-4"
                    density="compact"
                    size="x-large"
                    :model-value="userStars"
                    half-increments
                  />
                </div>
                <v-textarea 
                  variant="outlined"
                  color="primary"
                  class="mb-3"
                  id="reviewTextArea"
                  v-model="review"
                ></v-textarea>
                <div class="d-flex justify-end">
                  <v-btn color="primary" @click="postReview">게시하기</v-btn>
                </div>
              </v-sheet>
              <Comments ref="commentsComponentRef"/>
            </v-sheet>

          </div>
        </v-expand-transition>

        <v-expand-transition class="contents">
          <div v-show="showCharacterView === true">
            <CharacterVote v-bind:id="id" v-bind:type="type" @hide-character-view="onHideCharacterView"/>
          </div>
        </v-expand-transition>

      </v-col>
    </v-row>
  </v-container>

</template>

<style scoped>
  #container {
    background-color: #F8FAFD;
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

  import { useRoute, useRouter } from 'vue-router';
  import { ref, reactive, onMounted, computed } from 'vue'
  import axios from 'axios'
  import MediaDetailBackdop from './MediaDetailBackdop.vue';
  import CharacterVote from './CharacterVote.vue';
  import { useCheckAuthenticated } from '@/composables/checkAuthenticated';
  import { useNavigationStore } from '@/composables/stores/navigation';
  import { useReviewSaveStore } from '@/composables/stores/reviewSave';
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  
  let showCharacterView = ref(false)
  const id = ref(useRoute().query.id) // 주소창에서 id 쿼리를 얻어옴
  const type = ref(useRoute().query.type)

  function goToCharacterVote() {
    showCharacterView.value = true
  }

  function onHideCharacterView() {
    showCharacterView.value = false
  }

  let mediaInfo = ref({})
  /*
      "logo_sizes": [
      "w45",
      "w92",
      "w154",
      "w185",
      "w300",
      "w500",
      "original"
    ],
  */
  let contentProviderImageBaseUrl = "http://image.tmdb.org/t/p/w45"
  let hasWatched = ref(false)
  let videoPlaying = ref(false)

  // axios 요청
  // working...
  async function getResponse() {
    let response = await axios.get(
      apiBaseUrl + '/api/detail',
      {
        // TODO : 바꿔야 함
        params : {
          type: type.value,
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

  let commentsComponentRef = ref(null)
  let route = useRoute()
  let router = useRouter()
  let review = ref("")
  let userStars = ref(0.0)
  onMounted(() => {
    const reviewStore = useReviewSaveStore()
    review.value = reviewStore.draft.content
    reviewStore.clearDraft()
  })

  let postReview = async () => {
    let reviewTextArea = document.getElementById("reviewTextArea")

    const isLogined = await useCheckAuthenticated(); // 결과를 기다림
    if (!isLogined) {
      const reviewStore = useReviewSaveStore()
      reviewStore.setDraft(reviewTextArea.value)
      const navigationStore = useNavigationStore()
      navigationStore.setPreviousPage(route.fullPath)
      router.push({
        path: '/login'
      })
      return
    }

    // console.log(`userStars : ${userStars.value}, typeof userStars : ${typeof userStars.value}`)

    let mediaId = route.query.id // string 타입
    let mediaType = route.query.type

    // working...
    let response = await axios.post(
      apiBaseUrl + '/api/review',
      null,
      {
        withCredentials: true,
        headers: {
          "X-Requested-With": "XMLHttpRequest"
        },
        params : {
          mediaId: mediaId, // String
          mediaType: mediaType, // String
          stars : userStars.value, // Number
          text: reviewTextArea.value, // String
        }
      }
    )
    console.log("========postReview() response : ========")
    console.log(response)
    console.log("========postReview() response end ========")
    review.value = ""
    // TODO : 자식 컴포넌트의 이벤트를 실행하는 방식으로 바꾸기
    if (commentsComponentRef.value) {
      commentsComponentRef.value.fetchComments(mediaId, 0, "no"); // 최신순 정렬하고 첫 페이지 보여줌
    }
  }


  let logRef = () => {
    console.log(contentProviderImageElements.value)
  }

  let getContentProviders = computed(() => {
    if (!(mediaInfo.value)["watch/providers"]) { return [] }
    let providerInfos = (mediaInfo.value)["watch/providers"]["results"]["KR"]
    console.log('+++++++++++++++++++++++++++++++++providerInfos ...')
    console.log(providerInfos)
    console.log('+++++++++++++++++++++++++++++++++providerInfos end')
    if (!providerInfos) {return []}
    let providerInfoArray = []
    if (providerInfos.rent && Array.isArray(providerInfos.rent)) { providerInfoArray = providerInfoArray.concat(providerInfos.rent) }
    if (providerInfos.buy && Array.isArray(providerInfos.buy)) { providerInfoArray = providerInfoArray.concat(providerInfos.buy) }
    if (providerInfos.flatrate && Array.isArray(providerInfos.flatrate)) { providerInfoArray = providerInfoArray.concat(providerInfos.flatrate) }
    // 중복 제거 알고리즘
    let nameSet = new Set(providerInfoArray.map((p) => {
        return p.provider_name
      })
    )
    let filteredProviderInfoArray = []
    for (name of nameSet) {
      console.log(`providerName : ${name}`)
      console.log(`index : ${providerInfoArray.findIndex((p) => {return p.provider_name === name})}`)
      if (providerInfoArray.findIndex((p) => {return p.provider_name === name}) === -1) {continue}
      filteredProviderInfoArray.push( providerInfoArray[providerInfoArray.findIndex((p) => {return p.provider_name === name})] )
    }
    // 중복 제거 알고리즘 END
    console.log("===providerInfoArray : ")
    console.log(mediaInfo.value)
    console.log(providerInfoArray)
    console.log(filteredProviderInfoArray)
    console.log("===providerInfoArray END")
    return filteredProviderInfoArray
  })

  let hasContentProvider = () => {
    return getContentProviders.value.length > 0
  }

  let getProviderLogoPath = (index) => {
    return contentProviderImageBaseUrl + getContentProviders.value[index]["logo_path"]
  }
</script>