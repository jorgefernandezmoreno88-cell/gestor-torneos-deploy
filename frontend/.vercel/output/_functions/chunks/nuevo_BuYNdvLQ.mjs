import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { l as renderComponent, r as renderTemplate, m as maybeRenderHead } from './entrypoint_BgRex8wb.mjs';
import { $ as $$MainLayout, r as renderScript } from './MainLayout_pCS74QOT.mjs';

const $$Nuevo = createComponent(async ($$result, $$props, $$slots) => {
  return renderTemplate`${renderComponent($$result, "MainLayout", $$MainLayout, { "title": "Nuevo Torneo - Gestor de Torneos", "description": "Crear torneo", "data-astro-cid-tbuuyy5o": true }, { "default": async ($$result2) => renderTemplate` ${maybeRenderHead()}<div class="container form-container" data-astro-cid-tbuuyy5o> <div class="card" data-astro-cid-tbuuyy5o> <h1 data-astro-cid-tbuuyy5o>Crear torneo</h1> <div id="torneo-message" class="alert" style="display:none;" data-astro-cid-tbuuyy5o></div> <form id="torneo-form" class="torneo-form" data-astro-cid-tbuuyy5o> <label for="nombre" data-astro-cid-tbuuyy5o>Nombre</label> <input id="nombre" name="nombre" required data-astro-cid-tbuuyy5o> <label for="descripcion" data-astro-cid-tbuuyy5o>Descripción</label> <textarea id="descripcion" name="descripcion" data-astro-cid-tbuuyy5o></textarea> <label for="fechaInicio" data-astro-cid-tbuuyy5o>Fecha inicio</label> <input id="fechaInicio" name="fechaInicio" type="datetime-local" required data-astro-cid-tbuuyy5o> <label for="fechaFin" data-astro-cid-tbuuyy5o>Fecha fin</label> <input id="fechaFin" name="fechaFin" type="datetime-local" required data-astro-cid-tbuuyy5o> <label for="formato" data-astro-cid-tbuuyy5o>Formato</label> <select id="formato" name="formato" required data-astro-cid-tbuuyy5o> <option value="SUIZO" data-astro-cid-tbuuyy5o>SUIZO</option> <option value="ELIMINATORIA" data-astro-cid-tbuuyy5o>ELIMINATORIA</option> <option value="LIGA" data-astro-cid-tbuuyy5o>LIGA</option> </select> <label for="nivel" data-astro-cid-tbuuyy5o>Nivel</label> <select id="nivel" name="nivel" required data-astro-cid-tbuuyy5o> <option value="PRINCIPIANTE" data-astro-cid-tbuuyy5o>PRINCIPIANTE</option> <option value="INTERMEDIO" data-astro-cid-tbuuyy5o>INTERMEDIO</option> <option value="AVANZADO" data-astro-cid-tbuuyy5o>AVANZADO</option> </select> <label for="plazasMaximas" data-astro-cid-tbuuyy5o>Plazas máximas</label> <input id="plazasMaximas" name="plazasMaximas" type="number" min="2" required data-astro-cid-tbuuyy5o> <label for="numRondas" data-astro-cid-tbuuyy5o>Número de rondas</label> <input id="numRondas" name="numRondas" type="number" min="1" required data-astro-cid-tbuuyy5o> <label for="costeInscripcion" data-astro-cid-tbuuyy5o>Coste inscripción</label> <input id="costeInscripcion" name="costeInscripcion" type="number" min="0" step="0.01" required data-astro-cid-tbuuyy5o> <button class="button button-primary" type="submit" data-astro-cid-tbuuyy5o>Crear torneo</button> </form> </div> </div> ` })} ${renderScript($$result, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/torneos/nuevo.astro?astro&type=script&index=0&lang.ts")}`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/torneos/nuevo.astro", void 0);

const $$file = "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/torneos/nuevo.astro";
const $$url = "/torneos/nuevo";

const _page = /*#__PURE__*/Object.freeze(/*#__PURE__*/Object.defineProperty({
  __proto__: null,
  default: $$Nuevo,
  file: $$file,
  url: $$url
}, Symbol.toStringTag, { value: 'Module' }));

const page = () => _page;

export { page };
