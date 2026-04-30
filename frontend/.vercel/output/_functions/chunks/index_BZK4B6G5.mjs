import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { l as renderComponent, r as renderTemplate, m as maybeRenderHead } from './entrypoint_BgRex8wb.mjs';
import { $ as $$MainLayout } from './MainLayout_pCS74QOT.mjs';
import { e as getTorneos } from './api_CYCyv3yI.mjs';
import { $ as $$TorneoCard } from './TorneoCard_BESyE3O5.mjs';

const $$Index = createComponent(async ($$result, $$props, $$slots) => {
  let torneos = [];
  let error = null;
  try {
    torneos = await getTorneos();
  } catch (err) {
    error = "No se pudieron cargar los torneos. Verifica que el backend esté en funcionamiento en http://localhost:8080";
    console.error(err);
  }
  const torneosSeguros = Array.isArray(torneos) ? torneos.filter(Boolean) : [];
  return renderTemplate`${renderComponent($$result, "MainLayout", $$MainLayout, { "title": "Inicio - Gestor de Torneos", "description": "Gestiona tus torneos deportivos", "data-astro-cid-j7pv25f6": true }, { "default": async ($$result2) => renderTemplate` ${maybeRenderHead()}<div class="container" data-astro-cid-j7pv25f6> <section class="hero" data-astro-cid-j7pv25f6> <h1 data-astro-cid-j7pv25f6>Gestor de Torneos</h1> <p data-astro-cid-j7pv25f6>Organiza, gestiona y sigue tus torneos deportivos de forma sencilla</p> <a href="/torneos" class="button button-primary" data-astro-cid-j7pv25f6>Explorar Torneos</a> </section> <section class="torneos-preview" data-astro-cid-j7pv25f6> <h2 data-astro-cid-j7pv25f6>Últimos Torneos</h2> ${error && renderTemplate`<div class="alert alert-error" data-astro-cid-j7pv25f6>
⚠️ ${error} </div>`} ${torneosSeguros.length === 0 && !error ? renderTemplate`<p class="empty-state" data-astro-cid-j7pv25f6>No hay torneos</p>` : renderTemplate`<div class="torneos-grid" data-astro-cid-j7pv25f6> ${torneosSeguros.slice(0, 3).map((torneo) => renderTemplate`${renderComponent($$result2, "TorneoCard", $$TorneoCard, { "torneo": torneo, "data-astro-cid-j7pv25f6": true })}`)} </div>`} ${torneosSeguros.length > 3 && renderTemplate`<div class="text-center mt-lg" data-astro-cid-j7pv25f6> <a href="/torneos" class="button button-secondary" data-astro-cid-j7pv25f6>
Ver todos los torneos (${torneosSeguros.length})
</a> </div>`} </section> </div> ` })}`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/index.astro", void 0);

const $$file = "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/index.astro";
const $$url = "";

const _page = /*#__PURE__*/Object.freeze(/*#__PURE__*/Object.defineProperty({
  __proto__: null,
  default: $$Index,
  file: $$file,
  url: $$url
}, Symbol.toStringTag, { value: 'Module' }));

const page = () => _page;

export { page };
