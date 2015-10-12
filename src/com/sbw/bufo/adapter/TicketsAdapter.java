package com.sbw.bufo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.provider.SyncStateContract.Constants;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.sbw.bufo.R;
import com.sbw.bufo.dataset.TicketsDataItem;

public class TicketsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater = null;
	ArrayList<TicketsDataItem> orig;
	private ArrayList<TicketsDataItem> arrTickets;

	public TicketsAdapter(Context context, ArrayList<TicketsDataItem> arrTickets) {
		this.context = context;
		this.arrTickets = arrTickets;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void loadMore(ArrayList<TicketsDataItem> arrTickets) {
		this.arrTickets = arrTickets;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return arrTickets != null ? arrTickets.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return arrTickets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		TicketsViewHolder viewHolder;
		if (convertView == null) {

			v = inflater.inflate(R.layout.inflate_tickets, null);
			viewHolder = new TicketsViewHolder(v);
			v.setTag(viewHolder);
		} else {
			viewHolder = (TicketsViewHolder) v.getTag();
		}
		if (!TextUtils.isEmpty(arrTickets.get(position).getTicket_ID())) {
			viewHolder.txt_Ticket.setText(arrTickets.get(position)
					.getTicket_ID());
		}
		if (!TextUtils.isEmpty(arrTickets.get(position).getMobile())) {
			viewHolder.txt_Mobile.setText(arrTickets.get(position).getMobile());
		}
		if (!TextUtils.isEmpty(arrTickets.get(position).getIdentifier())) {
			viewHolder.txt_Identifier.setText(arrTickets.get(position)
					.getIdentifier());
		}
		if (!TextUtils.isEmpty(arrTickets.get(position).getStartTime())) {
			viewHolder.txt_StartDate.setText(arrTickets.get(position)
					.getStartTime());
		}

		return v;
	}

	public Filter getFilter() {
		return new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " +
				// constraint);
				arrTickets = (ArrayList<TicketsDataItem>) results.values;
				TicketsAdapter.this.notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				final FilterResults oReturn = new FilterResults();
				final ArrayList<TicketsDataItem> results = new ArrayList<TicketsDataItem>();
				if (orig == null)
					orig = arrTickets;
				if (constraint != null) {
					if (orig != null && orig.size() > 0) {
						for (final TicketsDataItem g : orig) {
							if (g.getTicket_ID().toLowerCase()
									.contains(constraint.toString())
									|| g.getMobile().toLowerCase()
											.contains(constraint.toString())
									|| g.getIdentifier().toLowerCase()
											.contains(constraint.toString())
									|| g.getStartTime().toLowerCase()
											.contains(constraint.toString()))
								results.add(g);
						}
					}
					oReturn.values = results;
				}
				return oReturn;
			}
		};
	}

	public class TicketsViewHolder {
		private TextView txt_Ticket;
		private TextView txt_Mobile;
		private TextView txt_Identifier;
		private TextView txt_StartDate;

		public TicketsViewHolder(View base) {
			txt_Ticket = (TextView) base.findViewById(R.id.txt_ticketid);
			txt_Mobile = (TextView) base.findViewById(R.id.txt_mobile);
			txt_Identifier = (TextView) base.findViewById(R.id.txt_identifier);
			txt_StartDate = (TextView) base.findViewById(R.id.txt_startdate);
		}

	}

}
