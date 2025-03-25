import axios from 'axios'

export async function useCheckAuthenticated() {
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL

  let response = await axios.get(
    apiBaseUrl + '/api/authenticated',
    {
      withCredentials: true,

      headers: {
        "X-Requested-With": "XMLHttpRequest"
      }
    }
  ) // axios.get end
  console.log(`authenticated? : ${response.data && response.data.authenticated == true}`)
  return response.data && response.data.authenticated == true
}