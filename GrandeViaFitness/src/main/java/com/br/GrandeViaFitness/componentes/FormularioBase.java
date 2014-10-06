package com.br.GrandeViaFitness.componentes;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public class FormularioBase<T> extends Form<T>
{
   private static final long serialVersionUID = -5262964465965255989L;

   public FormularioBase(final String id, final IModel<T> model)
   {
      super(id, model);
   }

   public FormularioBase(final String id)
   {
      super(id);
   }

   @Override
   protected void onSubmit()
   {
   };

   public void atualizaTela(final AjaxRequestTarget target, final Component... component)
   {
      target.appendJavaScript("addScriptCss();");
      for (final Component componente : component)
      {
         if (componente.getClass().getSimpleName().equals("FeedbackPanel"))
         {
            target.appendJavaScript("setTimeout(function(){$('#" + componente.getMarkupId() + "').hide('slow')}, 8000);");
         }
      }
      target.add(component);
   }
}
