<template>
  <!-- 자식에서 "requestOpenLoginModal"라는 emit이 오면 자신 안에 정의된 requestOpenLoginModal() 함수로 보냄 -->
  <MediaDetailBackdop 
    v-bind:mediaInfo="mediaInfo"
    @requestOpenLoginModal="requestOpenLoginModal" 
  />
  <v-container fluid id="container">
    <v-row justify="center">
      <v-col lg="8" cols="12">
        <v-expand-transition class="contents">
          <div v-show="showCharacterView === false">

            <v-sheet v-if="mediaInfo.ai_summary" border class="mt-3 mb-3 pa-3 rounded-lg">
              <div class="pretender-headline">🧐 아는 척</div>
              <div><span class="pretender-subline">"AI의 똑똑한 감상 요약으로 지인들에게 '아는 척'을 해보세요!"</span></div>
              <v-sheet border class="mt-3 mb-3 pa-3 rounded-lg">
                <div v-if="mediaInfo.ai_summary.onePoint">
                  <h1>원포인트</h1>
                  <div class="pretender-directive">친구에게 이 한마디로 대화를 시작해보는 건 어떨까요?</div>
                  <div class="pretender-onepoint mt-3 mb-3">{{ "\"" + mediaInfo.ai_summary.onePoint + "\"" }}</div>
                </div>
                <div class="mt-1 mb-1" v-for="categoryKey in Object.keys(mediaInfo.ai_summary).filter((key) => key.startsWith('category') && mediaInfo.ai_summary[key])" :key="categoryKey">
                  <h2 class="mb-1">{{ mediaInfo.ai_summary_category[Number(categoryKey.replace("category", ""))] }}</h2>
                  <p>{{ mediaInfo.ai_summary[categoryKey] }}</p>
                </div>
              </v-sheet>
            </v-sheet>

            <v-sheet v-if="hasContentProvider()" border class="mt-3 mb-3 pa-3 rounded-lg">
              <h2>다음과 같은 플랫폼에서 볼 수 있어요</h2>
              <div class="d-flex align-center ga-3">
                <img
                  v-for="( _ , i) in getContentProviders"
                  :src="getProviderLogoPath(i)"
                ><img>
              </div>
            </v-sheet>

            <!-- {{ JSON.stringify(mostVotedCharacter) }} -->
            <v-sheet v-if="mostVotedCharacter.apiFetched" border class="mt-3 mb-3 pa-3 rounded-lg">
              <h2>이 작품에서 가장 인기있는 캐릭터는?</h2>
              <div v-if="mostVotedCharacter.result">
                <v-row>
                  <v-col v-if="smAndUp" cols="3">
                    <img class="most-voted-character-profile-image" :src="profileBaseUrl + mostVotedCharacter.result.profilePath"/>
                  </v-col>
                  <v-col cols="9">
                    <div class="d-flex flex-column h-100">
                      <div class="vote-panel-line1">
                        <h1 class="mt-5">{{ mostVotedCharacter.result.characterName }}</h1>
                        <div>{{ `(배우 : ${mostVotedCharacter.result.actorName})` }}</div>
                      </div>
                      <div  class="vote-panel-line2 flex-grow-1">
                        <v-btn color="primary" class="go-to-vote-btn" @click="goToCharacterVote">투표하러 가기</v-btn>
                      </div>
                      <!-- <div  class="vote-panel-line2 flex-grow-1">
                        <v-btn color="primary">투표결과 보기</v-btn>
                      </div> -->
                    </div>
                  </v-col>
                </v-row>
              </div>
              <div v-else>
                <div class="d-flex justify-center">
                  <div style="font-size: 120px; filter: grayscale(100%);">🤔</div>
                </div>
                <div class="d-flex justify-center align-center">
                  <h3>아직 투표한 사람이 없어요</h3>
                  <span class="ml-3"> <a href="javascript:void(0);" @click="goToCharacterVote">👉투표하러 가기</a></span>
                </div>
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
            <CharacterVote 
              :id="id" 
              :type="type" 
              v-model:voteChartDialogProxy="voteChartDialogProxy"
              v-model:mostVotedCharacter="mostVotedCharacter"
              @hide-character-view="onHideCharacterView" 
              @requestOpenLoginModal="requestOpenLoginModal"/>
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

  .pretender-headline {
    font-size: 60px;
    font-weight: bold;
  }

  .pretender-subline {
    font-size: 24px;
    font-style: italic;
    background-color: #D2F9F4;
  }

  .pretender-directive {
    font-style: italic;
  }

  .pretender-onepoint {
    font-size: 24px;
    text-align: center;
    background-color: #d1d1d1;
  }

  .most-voted-character-profile-image {
    width: 100%;
    height: 250px;
    display: block;
    object-fit: contain;
    object-position: center;
  }

  .vote-panel-line2 {
    display: flex;
    justify-content: center;
    padding-top: 12px;
    padding-left: 48px;
    padding-right: 48px;
  }

  .go-to-vote-btn {
    width: 100%;
  }
</style>

<script setup>
  import axios from 'axios'
  import { useRoute, useRouter } from 'vue-router';
  import { ref, reactive, onMounted, computed } from 'vue'
  import { useDisplay } from 'vuetify'
  import { useCheckAuthenticated } from '@/composables/checkAuthenticated';
  import { useNavigationStore } from '@/composables/stores/navigation';
  import { useReviewSaveStore } from '@/composables/stores/reviewSave';
  import MediaDetailBackdop from './MediaDetailBackdop.vue';
  import CharacterVote from './CharacterVote.vue';

  const props = defineProps({
    voteChartDialog: Object
  })
  const emit = defineEmits(["requestOpenLoginModal", "update:voteChartDialog"])
  const voteChartDialogProxy = computed({
    get: () => props.voteChartDialog,
    set: (val) => emit("update:voteChartDialog", val) // 자식컴포넌트에서 voteChartDialogProxy = val을 할 시 여기서 emit이 호출된다.
  })
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const profileBaseUrl = "http://image.tmdb.org/t/p/w185"
  const contentProviderImageBaseUrl = "http://image.tmdb.org/t/p/w45"
  const { smAndUp } = useDisplay()
  const route = useRoute()
  const router = useRouter()
  const reviewStore = useReviewSaveStore()
  const showCharacterView = ref(false)
  const id = ref(useRoute().query.id) // 주소창에서 id 쿼리를 얻어옴
  const type = ref(useRoute().query.type)
  const mediaInfo = ref({})
  const commentsComponentRef = ref(null)
  const review = ref("")
  const userStars = ref(0.0)
  const getContentProviders = computed(() => {
    if (!(mediaInfo.value)["watch/providers"]) { return [] }
    let providerInfos = (mediaInfo.value)["watch/providers"]["results"]["KR"]
    if (!providerInfos) {return []}
    let providerInfoArray = []
    if (providerInfos.rent && Array.isArray(providerInfos.rent)) { providerInfoArray = providerInfoArray.concat(providerInfos.rent) }
    if (providerInfos.buy && Array.isArray(providerInfos.buy)) { providerInfoArray = providerInfoArray.concat(providerInfos.buy) }
    if (providerInfos.flatrate && Array.isArray(providerInfos.flatrate)) { providerInfoArray = providerInfoArray.concat(providerInfos.flatrate) }
    
    // 중복 제거 알고리즘
    let nameSet = new Set(providerInfoArray.map((p) =>  p.provider_name))
    let filteredProviderInfoArray = []
    for (name of nameSet) {
      if (providerInfoArray.findIndex((p) => p.provider_name === name) === -1) continue;
      filteredProviderInfoArray.push(providerInfoArray[providerInfoArray.findIndex((p) => p.provider_name === name)])
    }
    return filteredProviderInfoArray
  })
  const mostVotedCharacter = ref({apiFetched: false});

  function requestOpenLoginModal() {
    console.log("MediaDetail에서 requestOpenLoginModal를 요청함")
    emit("requestOpenLoginModal")
  }
  function goToCharacterVote() {
    showCharacterView.value = true
  }
  function onHideCharacterView() {
    showCharacterView.value = false
  }
  let hasContentProvider = () => {
    return getContentProviders.value.length > 0
  }
  let getProviderLogoPath = (index) => {
    return contentProviderImageBaseUrl + getContentProviders.value[index]["logo_path"]
  }
  let postReview = async () => {
    let reviewTextArea = document.getElementById("reviewTextArea")
    const isLogined = await useCheckAuthenticated(); // 결과를 기다림
    if (!isLogined) {
      // deprecated
      // reviewStore.setDraft(reviewTextArea.value)
      // navigationStore.setPreviousPage(route.fullPath)
      // router.push({
      //   path: '/login'
      // })
      emit("requestOpenLoginModal")
      return
    }

    let mediaId = route.query.id // string 타입
    let mediaType = route.query.type
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
    review.value = ""
    commentsComponentRef?.value.fetchComments(mediaId, 0, "no"); // 최신순 정렬하고 첫 페이지 보여줌
  }
  async function getResponse() {
    let response = await axios.get(
      apiBaseUrl + '/api/detail',
      {
        params : {
          type: type.value,
          mediaId: id.value,
        }
      }
    ) // axios.get end
    mediaInfo.value = response.data
    console.log('mediaInfo', mediaInfo.value)
  }

  onMounted(() => {
    review.value = reviewStore.draft.content
    reviewStore.clearDraft()
  })

  getResponse()
</script>