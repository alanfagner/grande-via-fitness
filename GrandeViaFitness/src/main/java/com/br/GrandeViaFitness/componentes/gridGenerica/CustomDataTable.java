package com.br.GrandeViaFitness.componentes.gridGenerica;

import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.DataGridView;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IStyledColumn;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.IItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import com.br.GrandeViaFitness.componentes.CustomPagingNavigator;

public class CustomDataTable<T, S> extends Panel implements IPageableItems
{
   static abstract class CssAttributeBehavior extends Behavior
   {
      private static final long serialVersionUID = 1L;

      protected abstract String getCssClass();

      /** @see Behavior#onComponentTag(Component, ComponentTag) */
      @Override
      public void onComponentTag(final Component component, final ComponentTag tag)
      {
         final String className = getCssClass();
         if (!Strings.isEmpty(className))
         {
            tag.append("class", className, " ");
         }
      }
   }

   private static final long serialVersionUID = 1L;

   private final DataGridView<T> datagrid;

   private final WebMarkupContainer body;

   private final List<? extends IColumn<T, S>> columns;

   private final ToolbarsContainer topToolbars;

   private final ToolbarsContainer bottomToolbars;

   private final Caption caption;

   private long toolbarIdCounter;

   /** Constructor
    *
    * @param id component id
    * @param columns list of IColumn objects
    * @param dataProvider imodel for data provider
    * @param rowsPerPage number of rows per page */
   public CustomDataTable(final String id, final List<? extends IColumn<T, S>> columns, final IDataProvider<T> dataProvider,
      final long rowsPerPage)
   {
      super(id);

      Args.notEmpty(columns, "columns");

      this.columns = columns;
      this.caption = new Caption("caption", getCaptionModel());
      add(caption);
      body = newBodyContainer("body");
      datagrid = newDataGridView("rows", columns, dataProvider);
      datagrid.setItemsPerPage(rowsPerPage);
      body.add(datagrid);
      add(body);
      topToolbars = new ToolbarsContainer("topToolbars");
      bottomToolbars = new ToolbarsContainer("bottomToolbars");
      add(topToolbars);
      add(bottomToolbars);
   }

   /** Factory method for the DataGridView
    *
    * @param id The component id
    * @param columns list of IColumn objects
    * @param dataProvider imodel for data provider
    * @return the data grid view */
   protected DataGridView<T> newDataGridView(final String id, final List<? extends IColumn<T, S>> columns,
      final IDataProvider<T> dataProvider)
   {
      return new DefaultDataGridView(id, columns, dataProvider);
   }

   /** Returns the model for table's caption. The caption wont be rendered if the model has empty value.
    *
    * @return the model for table's caption */
   protected IModel<String> getCaptionModel()
   {
      return null;
   }

   /** Create the MarkupContainer for the <tbody> tag. Users may subclass it to provide their own (modified) implementation.
    *
    * @param id
    * @return A new markup container */
   protected WebMarkupContainer newBodyContainer(final String id)
   {
      return new WebMarkupContainer(id);
   }

   /** Set the 'class' attribute for the tbody tag.
    *
    * @param cssStyle */
   public final void setTableBodyCss(final String cssStyle)
   {
      body.add(AttributeModifier.replace("class", cssStyle));
   }

   /** Adds a toolbar to the datatable that will be displayed after the data
    *
    * @param toolbar toolbar to be added
    *
    * @see AbstractToolbar */
   public void addBottomToolbar(final AbstractToolbar toolbar)
   {
      addToolbar(toolbar, bottomToolbars);
   }

   /** Adds a toolbar to the datatable that will be displayed before the data
    *
    * @param toolbar toolbar to be added
    *
    * @see AbstractToolbar */
   public void addTopToolbar(final AbstractToolbar toolbar)
   {
      addToolbar(toolbar, topToolbars);
   }

   public void addTopToolbar(final CustomPagingNavigator<T, S> toolbar)
   {
      addToolbar(toolbar, topToolbars);
   }

   /** @return the container with the toolbars at the top */
   public final ToolbarsContainer getTopToolbars()
   {
      return topToolbars;
   }

   /** @return the container with the toolbars at the bottom */
   public final ToolbarsContainer getBottomToolbars()
   {
      return bottomToolbars;
   }

   /** @return the container used for the table body */
   public final WebMarkupContainer getBody()
   {
      return body;
   }

   /** @return the component used for the table caption */
   public final Caption getCaption()
   {
      return caption;
   }

   /** @return dataprovider */
   public final IDataProvider<T> getDataProvider()
   {
      return datagrid.getDataProvider();
   }

   /** @return array of column objects this table displays */
   public final List<? extends IColumn<T, S>> getColumns()
   {
      return columns;
   }

   /** @see org.apache.wicket.markup.html.navigation.paging.IPageable#getCurrentPage() */
   @Override
   public final long getCurrentPage()
   {
      return datagrid.getCurrentPage();
   }

   /** @see org.apache.wicket.markup.html.navigation.paging.IPageable#getPageCount() */
   @Override
   public final long getPageCount()
   {
      return datagrid.getPageCount();
   }

   /** @return total number of rows in this table */
   public final long getRowCount()
   {
      return datagrid.getRowCount();
   }

   /** @return number of rows per page */
   @Override
   public final long getItemsPerPage()
   {
      return datagrid.getItemsPerPage();
   }

   /** @see org.apache.wicket.markup.html.navigation.paging.IPageable#setCurrentPage(long) */
   @Override
   public final void setCurrentPage(final long page)
   {
      datagrid.setCurrentPage(page);
      onPageChanged();
   }

   /** Sets the item reuse strategy. This strategy controls the creation of {@link Item}s.
    *
    * @see RefreshingView#setItemReuseStrategy(IItemReuseStrategy)
    * @see IItemReuseStrategy
    *
    * @param strategy item reuse strategy
    * @return this for chaining */
   public final CustomDataTable<T, S> setItemReuseStrategy(final IItemReuseStrategy strategy)
   {
      datagrid.setItemReuseStrategy(strategy);
      return this;
   }

   /** Sets the number of items to be displayed per page
    *
    * @param items number of items to display per page */
   @Override
   public void setItemsPerPage(final long items)
   {
      datagrid.setItemsPerPage(items);
   }

   /** @see org.apache.wicket.markup.html.navigation.paging.IPageableItems#getItemCount() */
   @Override
   public long getItemCount()
   {
      return datagrid.getItemCount();
   }

   private void addToolbar(final AbstractToolbar toolbar, final ToolbarsContainer container)
   {
      Args.notNull(toolbar, "toolbar");

      container.getRepeatingView().add(toolbar);
   }

   private void addToolbar(final CustomPagingNavigator<T, S> toolbar, final ToolbarsContainer container)
   {
      Args.notNull(toolbar, "toolbar");

      container.getRepeatingView().add(toolbar);
   }

   /** Factory method for Item container that represents a cell in the underlying DataGridView
    *
    * @see Item
    *
    * @param id component id for the new data item
    * @param index the index of the new data item
    * @param model the model for the new data item
    *
    * @return DataItem created DataItem */
   protected Item<IColumn<T, S>> newCellItem(final String id, final int index, final IModel<IColumn<T, S>> model)
   {
      return new Item<IColumn<T, S>>(id, index, model);
   }

   /** Factory method for Item container that represents a row in the underlying DataGridView
    *
    * @see Item
    *
    * @param id component id for the new data item
    * @param index the index of the new data item
    * @param model the model for the new data item.
    *
    * @return DataItem created DataItem */
   protected Item<T> newRowItem(final String id, final int index, final IModel<T> model)
   {
      return new Item<T>(id, index, model);
   }

   /** @see org.apache.wicket.Component#onDetach() */
   @Override
   protected void onDetach()
   {
      super.onDetach();

      for (final IColumn<T, S> column : columns)
      {
         column.detach();
      }
   }

   /** Event listener for page-changed event */
   protected void onPageChanged()
   {
      // noop
   }

   /** @see AbstractToolbar */
   String newToolbarId()
   {
      toolbarIdCounter++;
      return String.valueOf(toolbarIdCounter).intern();
   }

   @Override
   protected void onComponentTag(final ComponentTag tag)
   {
      checkComponentTag(tag, "table");
      super.onComponentTag(tag);
   }

   /** This class acts as a repeater that will contain the toolbar. It makes sure that the table row group (e.g. thead) tags are only visible when they contain rows in accordance with the HTML specification.
    *
    * @author igor.vaynberg */
   private static class ToolbarsContainer extends WebMarkupContainer
   {
      private static final long serialVersionUID = 1L;

      private final RepeatingView toolbars;

      /** Constructor
       *
       * @param id */
      private ToolbarsContainer(final String id)
      {
         super(id);
         toolbars = new RepeatingView("toolbars");
         add(toolbars);
      }

      public RepeatingView getRepeatingView()
      {
         return toolbars;
      }

      @Override
      public void onConfigure()
      {
         super.onConfigure();

         toolbars.configure();

         Boolean visible = toolbars.visitChildren(new IVisitor<Component, Boolean>()
         {
            @Override
            public void component(final Component object, final IVisit<Boolean> visit)
            {
               object.configure();
               if (object.isVisible())
               {
                  visit.stop(Boolean.TRUE);
               }
               else
               {
                  visit.dontGoDeeper();
               }
            }
         });
         if (visible == null)
         {
            visible = false;
         }
         setVisible(visible);
      }
   }

   /** A caption for the table. It renders itself only if {@link DataTable#getCaptionModel()} has non-empty value. */
   private static class Caption extends Label
   {
      /**
       */
      private static final long serialVersionUID = 1L;

      /** Construct.
       *
       * @param id the component id
       * @param model the caption model */
      public Caption(final String id, final IModel<String> model)
      {
         super(id, model);
      }

      @Override
      protected void onConfigure()
      {
         setRenderBodyOnly(Strings.isEmpty(getDefaultModelObjectAsString()));

         super.onConfigure();
      }

      @Override
      protected IModel<String> initModel()
      {
         // don't try to find the model in the parent
         return null;
      }
   }

   private class DefaultDataGridView extends DataGridView<T>
   {
      private static final long serialVersionUID = 4917198911057279592L;

      public DefaultDataGridView(final String id, final List<? extends IColumn<T, S>> columns, final IDataProvider<T> dataProvider)
      {
         super(id, columns, dataProvider);
      }

      @SuppressWarnings({"rawtypes", "unchecked"})
      @Override
      protected Item newCellItem(final String id, final int index, final IModel model)
      {
         final Item item = CustomDataTable.this.newCellItem(id, index, model);
         final IColumn<T, S> column = CustomDataTable.this.columns.get(index);
         if (column instanceof IStyledColumn)
         {
            item.add(new CssAttributeBehavior()
            {
               private static final long serialVersionUID = 1L;

               @Override
               protected String getCssClass()
               {
                  return ((IStyledColumn<T, S>) column).getCssClass();
               }
            });
         }
         return item;
      }

      @Override
      protected Item<T> newRowItem(final String id, final int index, final IModel<T> model)
      {
         return CustomDataTable.this.newRowItem(id, index, model);
      }
   }
}
