export const getApiErrorMessage = (error, fallbackMessage) => {
  const responseData = error.response?.data
  const serverMessage =
    responseData?.message ||
    responseData?.error ||
    (typeof responseData === 'string' ? responseData : '')

  if (serverMessage) {
    return serverMessage
  }

  if (error.response?.status === 403) {
    return 'Backend rejected this request. Check that Spring Security allows /api/v1/auth/register and /api/v1/auth/login.'
  }

  if (error.code === 'ERR_NETWORK') {
    return 'Cannot reach the FinTrack backend. Start the Spring Boot server on port 8080, then try again.'
  }

  return error.message || fallbackMessage
}
