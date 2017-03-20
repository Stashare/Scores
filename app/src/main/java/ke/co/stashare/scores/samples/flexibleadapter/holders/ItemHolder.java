package ke.co.stashare.scores.samples.flexibleadapter.holders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractSectionableItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.IHolder;
import ke.co.stashare.scores.R;
import ke.co.stashare.scores.samples.flexibleadapter.models.ItemModel;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * The holder item is just a wrapper for the Model item.
 *
 * <p>Holder item can be used to display the same modelData in multiple RecyclerViews managed by
 * different Adapters, you can implement a derived IFlexible item to HOLD your data model object!</p>
 *
 * In this way you can separate the memory zones of the flags (enabled, expanded, hidden,
 * selectable) used by a specific Adapter, to be independent by another Adapter. For instance,
 * an item can be Shown and Expanded in a RV, while in the other RV can be Hidden or Not Expanded!
 *
 * @author Davide Steduto
 * @since 19/10/2016
 */
public class ItemHolder extends AbstractSectionableItem<ItemHolder.ItemViewHolder, HeaderHolder>
		implements IFilterable, IHolder<ItemModel> {

	private ItemModel model;

	/**
	 * The header item must in its bounds, it must implement IHeader, therefore: HeaderHolder!
	 */
	public ItemHolder(ItemModel model, HeaderHolder header) {
		super(header);
		this.model = model;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ItemHolder) {
			ItemHolder inItem = (ItemHolder) o;
			return model.equals(inItem.getModel());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return model.hashCode();
	}

	/**
	 * @return the model object
	 */
	@Override
	public ItemModel getModel() {
		return model;
	}

	/**
	 * Filter is applied to the model fields.
	 */
	@Override
	public boolean filter(String constraint) {
		return model.getTitle() != null && model.getTitle().toLowerCase().trim().contains(constraint) ||
				model.getSubtitle() != null && model.getSubtitle().toLowerCase().trim().contains(constraint);
	}

	@Override
	public int getLayoutRes() {
		return R.layout.recycler_holder_item;
	}

	@Override
	public ItemViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
		return new ItemViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
	}

	@Override
	public void bindViewHolder(final FlexibleAdapter adapter, ItemViewHolder holder, int position, List payloads) {
		holder.mTitle.setText(model.getTitle());
		holder.mSubtitle.setText(model.getSubtitle());
	}

	static class ItemViewHolder extends FlexibleViewHolder {

		@BindView(R.id.title)
		public TextView mTitle;
		@BindView(R.id.subtitle)
		public TextView mSubtitle;

		/**
		 * Default constructor.
		 */
		public ItemViewHolder(View view, FlexibleAdapter adapter) {
			super(view, adapter);
			ButterKnife.bind(this, view);
		}
	}

}