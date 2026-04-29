import type {
  Torneo,
  Ronda,
  Partida,
  Inscripcion,
  Clasificacion,
  Usuario,
} from '../types/index';

export const API_BASE_URL = 'http://localhost:8080/api';
const TOKEN_KEY = 'authToken';

/**
 * Función genérica para hacer fetch a la API
 * Maneja errores y logging automáticamente
 */
async function fetchAPI<T>(
  endpoint: string,
  options?: RequestInit
): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  const mergedHeaders = {
    'Content-Type': 'application/json',
    ...(options?.headers || {}),
  };

  try {
    console.log('[API] Request', {
      method: options?.method || 'GET',
      url,
    });
    const response = await fetch(url, {
      ...options,
      headers: mergedHeaders,
    });

    const rawText = await response.text();
    let parsedData: unknown = null;
    try {
      parsedData = rawText ? JSON.parse(rawText) : null;
    } catch (_e) {
      parsedData = rawText;
    }

    console.log('[API] Response', {
      url,
      status: response.status,
      ok: response.ok,
      data: parsedData,
    });

    if (!response.ok) {
      const errorData = (parsedData && typeof parsedData === 'object')
        ? (parsedData as Record<string, unknown>)
        : {};
      throw new Error(
        String(errorData.message || `API Error: ${response.status} ${response.statusText}`)
      );
    }

    return parsedData as T;
  } catch (error) {
    console.error(`Error en ${endpoint}:`, error);
    throw error;
  }
}

function getAuthHeaders(): Record<string, string> {
  if (typeof localStorage === 'undefined') {
    return {};
  }
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) {
    return {};
  }
  return { Authorization: `Bearer ${token}` };
}

// ===== TORNEOS =====

/**
 * Obtiene el listado de todos los torneos
 */
export async function getTorneos(): Promise<Torneo[]> {
  return fetchAPI<Torneo[]>('/torneos');
}

/**
 * Obtiene los detalles de un torneo específico
 */
export async function getTorneoById(id: number): Promise<Torneo> {
  const response = await fetchAPI<Torneo | { data?: Torneo }>(`/torneos/${id}`);
  if (response && typeof response === 'object' && 'data' in response && response.data) {
    return response.data;
  }
  return response as Torneo;
}

/**
 * Crea un nuevo torneo
 */
export async function crearTorneo(torneo: Partial<Torneo>): Promise<Torneo> {
  return fetchAPI<Torneo>('/torneos', {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify(torneo),
  });
}

/**
 * Actualiza el estado de un torneo
 */
export async function actualizarEstadoTorneo(
  id: number,
  estado: string
): Promise<Torneo> {
  return fetchAPI<Torneo>(`/torneos/${id}/estado`, {
    method: 'PATCH',
    headers: getAuthHeaders(),
    body: JSON.stringify({ estado }),
  });
}

export async function abrirTorneo(id: number): Promise<Torneo> {
  return fetchAPI<Torneo>(`/torneos/${id}/abrir`, {
    method: 'PATCH',
    headers: getAuthHeaders(),
  });
}

// ===== INSCRIPCIONES =====

/**
 * Obtiene las inscripciones de un torneo
 */
export async function getInscripcionesByTorneo(
  torneoId: number
): Promise<Inscripcion[]> {
  return fetchAPI<Inscripcion[]>(`/torneos/${torneoId}/inscripciones`, {
    headers: getAuthHeaders(),
  });
}

/**
 * Inscribe un usuario en un torneo
 */
export async function inscribirEnTorneo(
  torneoId: number,
  usuarioId: number
): Promise<Inscripcion> {
  return fetchAPI<Inscripcion>(`/torneos/${torneoId}/inscripciones`, {
    method: 'POST',
    headers: getAuthHeaders(),
    body: JSON.stringify({ usuarioId }),
  });
}

// ===== CLASIFICACIÓN =====

/**
 * Obtiene la clasificación de un torneo
 */
export async function getClasificacionByTorneo(
  torneoId: number
): Promise<Clasificacion[]> {
  return fetchAPI<Clasificacion[]>(`/torneos/${torneoId}/clasificacion`, {
    headers: getAuthHeaders(),
  });
}

// ===== RONDAS =====

/**
 * Obtiene las rondas de un torneo
 */
export async function getRondasByTorneo(torneoId: number): Promise<Ronda[]> {
  return fetchAPI<Ronda[]>(`/torneos/${torneoId}/rondas`, {
    headers: getAuthHeaders(),
  });
}

/**
 * Crea una nueva ronda en un torneo
 */
export async function crearRonda(
  torneoId: number,
  ronda: Partial<Ronda>
): Promise<Ronda> {
  return fetchAPI<Ronda>(`/torneos/${torneoId}/rondas`, {
    method: 'POST',
    body: JSON.stringify(ronda),
  });
}

export async function generarRondaInicial(torneoId: number): Promise<unknown> {
  return fetchAPI<unknown>(`/torneos/${torneoId}/generar-ronda-inicial`, {
    method: 'POST',
    headers: getAuthHeaders(),
  });
}

export async function iniciarRonda(rondaId: number): Promise<Ronda> {
  return fetchAPI<Ronda>(`/rondas/${rondaId}/iniciar`, {
    method: 'PATCH',
    headers: getAuthHeaders(),
  });
}

// ===== PARTIDAS =====

/**
 * Obtiene las partidas de un torneo
 */
export async function getPartidasByTorneo(torneoId: number): Promise<Partida[]> {
  return fetchAPI<Partida[]>(`/torneos/${torneoId}/partidas`, {
    headers: getAuthHeaders(),
  });
}

/**
 * Obtiene las partidas de una ronda específica
 */
export async function getPartidasByRonda(rondaId: number): Promise<Partida[]> {
  return fetchAPI<Partida[]>(`/rondas/${rondaId}/partidas`);
}

/**
 * Actualiza el resultado de una partida
 */
export async function actualizarResultadoPartida(
  partidaId: number,
  payload: { resultado: string; ganadorId?: number | null; empate?: boolean }
): Promise<Partida> {
  return fetchAPI<Partida>(`/partidas/${partidaId}/resultado`, {
    method: 'PATCH',
    headers: getAuthHeaders(),
    body: JSON.stringify(payload),
  });
}

/**
 * Valida una partida (la marca como finalizada)
 */
export async function validarPartida(partidaId: number): Promise<Partida> {
  return fetchAPI<Partida>(`/partidas/${partidaId}/validar`, {
    method: 'PATCH',
    headers: getAuthHeaders(),
  });
}

// ===== AUTH (opcionales) =====

/**
 * Login de usuario
 */
export async function login(email: string, password: string): Promise<Usuario & { token: string }> {
  return fetchAPI<Usuario & { token: string }>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
}

/**
 * Registro de usuario
 */
export async function register(usuario: {
  nombre: string;
  email: string;
  password: string;
}): Promise<Usuario> {
  return fetchAPI<Usuario>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(usuario),
  });
}

/**
 * Obtiene el usuario autenticado
 */
export async function getCurrentUser(): Promise<Usuario> {
  return fetchAPI<Usuario>('/auth/me', {
    headers: getAuthHeaders(),
  });
}
