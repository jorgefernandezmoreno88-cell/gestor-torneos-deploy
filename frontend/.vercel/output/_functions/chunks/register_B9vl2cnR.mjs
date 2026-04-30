import { c as createComponent } from './astro-component_0s_TtBMU.mjs';
import 'piccolore';
import { l as renderComponent, r as renderTemplate, m as maybeRenderHead } from './entrypoint_BgRex8wb.mjs';
import { $ as $$MainLayout, r as renderScript } from './MainLayout_pCS74QOT.mjs';

const $$Register = createComponent(async ($$result, $$props, $$slots) => {
  return renderTemplate`${renderComponent($$result, "MainLayout", $$MainLayout, { "title": "Registro - Gestor de Torneos", "description": "Crear cuenta", "data-astro-cid-qraosrxq": true }, { "default": async ($$result2) => renderTemplate` ${maybeRenderHead()}<div class="container auth-container" data-astro-cid-qraosrxq> <div class="card auth-card" data-astro-cid-qraosrxq> <h1 data-astro-cid-qraosrxq>Crear cuenta</h1> <p class="auth-subtitle" data-astro-cid-qraosrxq>
Regístrate para inscribirte y gestionar torneos.
</p> <div id="register-message" class="alert" style="display:none;" data-astro-cid-qraosrxq></div> <form id="register-form" class="auth-form" data-astro-cid-qraosrxq> <label for="nombre" data-astro-cid-qraosrxq>Nombre</label> <input id="nombre" name="nombre" type="text" required data-astro-cid-qraosrxq> <label for="email" data-astro-cid-qraosrxq>Email</label> <input id="email" name="email" type="email" required data-astro-cid-qraosrxq> <label for="password" data-astro-cid-qraosrxq>Password</label> <input type="password" id="password" name="password" title="Mínimo 8 caracteres, una mayúscula, una minúscula y un número" data-astro-cid-qraosrxq> <button type="submit" class="button button-primary" data-astro-cid-qraosrxq>Registrarme</button> </form> </div> </div> ` })} ${renderScript($$result, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/register.astro?astro&type=script&index=0&lang.ts")}`;
}, "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/register.astro", void 0);

const $$file = "D:/Descargas/gestor-torneos-clase-master/frontend/src/pages/register.astro";
const $$url = "/register";

const _page = /*#__PURE__*/Object.freeze(/*#__PURE__*/Object.defineProperty({
  __proto__: null,
  default: $$Register,
  file: $$file,
  url: $$url
}, Symbol.toStringTag, { value: 'Module' }));

const page = () => _page;

export { page };
