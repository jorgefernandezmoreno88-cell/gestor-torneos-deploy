const API_BASE_URL = "https://gestor-torneos-deploy-production.up.railway.app/api";
const TOKEN_KEY = "authToken";
async function fetchAPI(endpoint, options) {
  const url = `${API_BASE_URL}${endpoint}`;
  const mergedHeaders = {
    "Content-Type": "application/json",
    ...options?.headers || {}
  };
  try {
    console.log("[API] Request", {
      method: options?.method || "GET",
      url
    });
    const response = await fetch(url, {
      ...options,
      headers: mergedHeaders
    });
    const rawText = await response.text();
    let parsedData = null;
    try {
      parsedData = rawText ? JSON.parse(rawText) : null;
    } catch (_e) {
      parsedData = rawText;
    }
    console.log("[API] Response", {
      url,
      status: response.status,
      ok: response.ok,
      data: parsedData
    });
    if (!response.ok) {
      const errorData = parsedData && typeof parsedData === "object" ? parsedData : {};
      throw new Error(
        String(errorData.message || `API Error: ${response.status} ${response.statusText}`)
      );
    }
    return parsedData;
  } catch (error) {
    console.error(`Error en ${endpoint}:`, error);
    throw error;
  }
}
function getAuthHeaders() {
  if (typeof localStorage === "undefined") {
    return {};
  }
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) {
    return {};
  }
  return { Authorization: `Bearer ${token}` };
}
async function getTorneos() {
  return fetchAPI("/torneos");
}
async function getTorneoById(id) {
  const response = await fetchAPI(`/torneos/${id}`);
  if (response && typeof response === "object" && "data" in response && response.data) {
    return response.data;
  }
  return response;
}
async function getInscripcionesByTorneo(torneoId) {
  return fetchAPI(`/torneos/${torneoId}/inscripciones`, {
    headers: getAuthHeaders()
  });
}
async function getClasificacionByTorneo(torneoId) {
  return fetchAPI(`/torneos/${torneoId}/clasificacion`, {
    headers: getAuthHeaders()
  });
}
async function getRondasByTorneo(torneoId) {
  return fetchAPI(`/torneos/${torneoId}/rondas`, {
    headers: getAuthHeaders()
  });
}
async function getPartidasByTorneo(torneoId) {
  return fetchAPI(`/torneos/${torneoId}/partidas`, {
    headers: getAuthHeaders()
  });
}

export { API_BASE_URL as A, getClasificacionByTorneo as a, getRondasByTorneo as b, getPartidasByTorneo as c, getInscripcionesByTorneo as d, getTorneos as e, getTorneoById as g };
