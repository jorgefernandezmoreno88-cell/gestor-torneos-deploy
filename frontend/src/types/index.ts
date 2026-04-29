// Enums del backend (espejar con los del API)
export type EstadoTorneo = 'ABIERTO' | 'CERRADO' | 'EN_PROGRESO' | 'FINALIZADO';
export type FormatoTorneo = 'LIGA' | 'ELIMINATORIA' | 'SUIZA' | 'ROUND_ROBIN';
export type NivelTorneo = 'PRINCIPIANTE' | 'INTERMEDIO' | 'AVANZADO' | 'PROFESIONAL';
export type EstadoPartida = 'PENDIENTE' | 'EN_PROGRESO' | 'FINALIZADA';
export type EstadoRonda = 'NO_INICIADA' | 'EN_PROGRESO' | 'FINALIZADA';
export type EstadoInscripcion = 'INSCRITO' | 'RETIRADO';

// Tipos de entidades
export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  rol?: 'ADMIN' | 'ORGANIZADOR' | 'USUARIO';
  activo?: boolean;
}

export interface Torneo {
  id: number;
  nombre: string;
  descripcion: string;
  fechaInicio: string; // OffsetDateTime como ISO string
  fechaFin?: string;
  estado: EstadoTorneo;
  formato: FormatoTorneo;
  nivel: NivelTorneo;
  plazasMaximas: number;
  numRondas: number;
  costeInscripcion: number;
  fechaCreacion?: string;
  creadorId?: number;
  creadorNombre?: string;
}

export interface Ronda {
  id: number;
  torneoId: number;
  numero: number;
  estado: EstadoRonda;
  fechaInicio?: string;
  fechaFin?: string;
}

export interface Partida {
  id: number;
  rondaId: number;
  torneoId: number;
  usuario1Id: number;
  usuario1Nombre: string;
  usuario2Id: number;
  usuario2Nombre: string;
  ganadorId?: number;
  ganadorNombre?: string;
  resultado?: string; // Puede ser "USUARIO1", "USUARIO2", "EMPATE"
  estado: EstadoPartida;
  fechaPartida?: string;
}

export interface Inscripcion {
  id: number;
  torneoId: number;
  usuarioId: number;
  usuarioNombre?: string;
  fechaInscripcion: string;
  estado: EstadoInscripcion;
}

export interface Clasificacion {
  posicion: number;
  usuarioId: number;
  usuarioNombre: string;
  puntos: number;
  victorias: number;
  derrotas: number;
  empates: number;
}

// Tipo para respuestas genéricas de API
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  timestamp?: string;
}
