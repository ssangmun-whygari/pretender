<template>
  <v-sheet border class="mt-3 mb-3 pa-3 rounded-lg">
    <h2>캐릭터 통계 <a href="javascript:void(0);" @click="$emit('hideCharacterView')"><v-icon size="20" color="primary" icon="mdi-arrow-expand-right" /></a></h2>
    <h3>인기순위</h3>
    <LikeGraphPanel
      :media_id="id"
      :media_type="media_type"
      :cast_info = "castInfo"
    />
    <h2>캐릭터들</h2>

    <div v-if="totalItemLength > 0">
      <v-row v-for="n in Math.ceil(totalItemLength / denominator)" v-if="castInfo.length > 0">
        <v-col v-for="(item, index) in slicedCastInfo(castInfo, n - 1)" md="4" cols="12">
          <v-card class="cast-info-container pa-3" :hover="true" @click="expandPanel(n, (n - 1) * 3 + index)">
            <v-row>
              <v-col class="d-flex align-center" cols="5">
                <img class="cast-image" :src="getProfileImagePath(item.profile_path)"></img>
              </v-col>
              <v-col cols="7">
                {{ `${n}, ${index}` }}<v-card-title>{{ item.character }}</v-card-title>
                <v-card-subtitle>{{`(배우 : ${item.name})`}}</v-card-subtitle>
              </v-col>
            </v-row>
          </v-card>
        </v-col>
        <v-col v-show="panelExpanded[n - 1] === true" cols="12" v-if="true">
          {{ n - 1 }}
          <v-sheet :elevation="2" cols="12" class="pa-3">
            <v-row>
              <v-col cols="2">
                <img :src="getProfileImagePath(castInfo[currentActiveIndex].profile_path)"></img>
              </v-col>
              
              <v-col cols="10">
                <div v-if="likePerCharacterGraphPanelShown === false" class="d-flex flex-column justify-space-around h-100">
                  <div class="vote-panel-line1">
                    <h2>당신이 가장 마음에 드는 캐릭터는?</h2>
                  </div>
                  <div class="vote-panel-line2">
                    <v-row>
                      <v-col cols="10">
                        <h1>{{ castInfo[currentActiveIndex].character }}</h1>
                        <div>{{ `(배우 : ${castInfo[currentActiveIndex].name})` }}</div>
                      </v-col>
                      <v-col cols="2">
                        <a href="javascript:void(0);" @click="showLikePerCharacterGraphPanel">
                          <v-icon size="60" color="primary" icon="mdi-arrow-expand-right" />
                        </a>
                      </v-col>
                    </v-row>
                  </div>
                  <div class="vote-panel-line3">
                    <v-row>
                      <v-col cols="10">
                        <div v-if="characterLikeCategory.length > 0">
                          <v-chip v-for="(item, _) in characterLikeCategory">
                            {{ `#${item.content}` }}
                          </v-chip>
                        </div>
                      </v-col>
                      <v-col cols="2"><v-btn color="primary">투표하기</v-btn></v-col>
                    </v-row>
                  </div>
                </div>

                <div v-if="likePerCharacterGraphPanelShown === true">
                  <LikePerCharacterGraphPanel
                    :media_id="id"
                    :media_type="media_type"
                    :character_id="character_id"
                  />
                </div>
              </v-col>

            </v-row>
          </v-sheet>
        </v-col>
      </v-row>
    </div>
  </v-sheet>
</template>

<style scoped>
  a:link {
    width: 0;
    height: 0;
    margin: 0;
    padding: 0;
  }
  a:visited {
    width: 0;
    height: 0;
    margin: 0;
    padding: 0;
  }

  div {
    font-family: 'Pretendard-Regular', sans-serif;
  }

  .cast-image {
    max-height: 176px;
  }

  .cast-info-container {
    height: 200px;
  }

  .vote-panel-line1 {
    height: 20%;
  }

  .vote-panel-line2 {
    height: 60%;
  }

  .vote-panel-line3 {
    height: 20%;
  }
</style>

<script setup>
  import axios from 'axios'
  import { ref, toRef, computed, onMounted } from 'vue'
  import { useDisplay } from 'vuetify'
  import LikePerCharacterGraphPanel from './LikePerCharacterGraphPanel.vue'
  import LikeGraphPanel from './LikeGraphPanel.vue'

  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL;
  const profileBaseUrl = "http://image.tmdb.org/t/p/w185"
  const props = defineProps({
      id: String,
      type: String
  })
  const id = toRef(props, 'id')
  const type = toRef(props, 'type')
  const media_type = ref("tv") // TODO : 고쳐야 함
  const character_id = ref(0)

  const castInfo = ref([])
  const { mdAndUp } = useDisplay()
  let totalItemLength = ref(0)
  let denominator = computed(() => {
    if (mdAndUp.value) {
      return 3
    } else {
      return 1
    }
  })
  let slicedCastInfo = (array, lineNum) => {
    // lineNum은 0부터 시작
    if (mdAndUp.value) {
      return array.slice(lineNum * 3, Math.min(totalItemLength.value, (lineNum * 3) + 3))
    } else {
      return [array[lineNum]]
    }
  }
  let panelExpanded = ref([])
  let currentActiveIndex = ref(0)
  let characterLikeCategory = ref([])
  let likePerCharacterGraphPanelShown = ref(false)

  function expandPanel(lineNum, index) {
    if (mdAndUp.value) {
      console.log("######md이상")
      console.log(`######${lineNum}`)
      currentActiveIndex.value = index
    } else {
      console.log("######md이하")
      console.log(`######${lineNum}`)
    }
    likePerCharacterGraphPanelShown.value = false
    character_id.value = (castInfo.value)[index].id
    panelExpanded.value = new Array(panelExpanded.value.length).fill(false);
    (panelExpanded.value)[lineNum - 1] = true
  }

  function showLikePerCharacterGraphPanel() {
    likePerCharacterGraphPanelShown.value = true
  }

  onMounted(async () => {
    await requestCastInfo()
    await requestCharacterLikeCategory()
  })

  async function requestCastInfo() {
    let response = await axios.get(
      apiBaseUrl + '/api/detail/cast',
      {
        params : {
          type: type.value,
          mediaId: id.value,
        }
      }
    ) // axios.get end
    console.log("########## requestCastInfo() ##########")
    console.log(response)
    // TODO : 애니메이션하고 일반 tv 시리즈하고 다른 처리가 필요함
    castInfo.value = response.data.cast
    totalItemLength.value = response.data.cast.length
    panelExpanded.value = new Array(totalItemLength.value).fill(false);
    console.log(castInfo.value)
    console.log("########## requestCastInfo() END ##########")
  }

  async function requestCharacterLikeCategory() {
    let response = await axios.get(
      apiBaseUrl + '/api/detail/cast/likeCategory',
    ) // axios.get end
    console.log("########## requestCharacterLikeCategory() ##########")
    console.log(response)
    console.log(response.data)
    characterLikeCategory.value = response.data
    console.log("########## requestCharacterLikeCategory() END ##########")
  }

  function getProfileImagePath(path) {
    return profileBaseUrl + path
  }
</script>