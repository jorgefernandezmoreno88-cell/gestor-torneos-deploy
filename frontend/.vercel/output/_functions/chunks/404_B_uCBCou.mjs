import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { l as renderComponent, r as renderTemplate, m as maybeRenderHead } from './entrypoint_BgRex8wb.mjs';
import { $ as $$MainLayout } from './MainLayout_pCS74QOT.mjs';

const $$404 = createComponent(($$result, $$props, $$slots) => {
  return renderTemplate`${renderComponent($$result, "MainLayout", $$MainLayout, { "title": "Página no encontrada - Gestor de Torneos", "description": "La página que buscas no existe", "data-astro-cid-zetdm5md": true }, { "default": ($$result2) => renderTemplate` ${maybeRenderHead()}<div class="container" data-astro-cid-zetdm5md> <div class="error-container" data-astro-cid-zetdm5md> <div class="error-icon" data-astro-cid-zetdm5md>404</div> <h1 data-astro-cid-zetdm5md>Página no encontrada</h1> <p data-astro-cid-zetdm5md>Lo sentimos, la página que buscas no existe o ha sido eliminada.</p> <a href="/" class="button button-primary" data-astro-cid-zetdm5md>Volver al inicio</a> </div> </div> ` })}`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/404.astro", void 0);

const $$file = "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/404.astro";
const $$url = "/404";

const _page = /*#__PURE__*/Object.freeze(/*#__PURE__*/Object.defineProperty({
  __proto__: null,
  default: $$404,
  file: $$file,
  url: $$url
}, Symbol.toStringTag, { value: 'Module' }));

const page = () => _page;

export { page };
