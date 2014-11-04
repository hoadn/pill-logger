package uk.co.pilllogger.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleSectionedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context _context;
    private static final int SECTION_TYPE = 0;

    private boolean _valid = true;
    private int _sectionResourceId;
    private int _textResourceId;
    private LayoutInflater _layoutInflater;
    private RecyclerView.Adapter _baseAdapter;
    private SparseArray<Section> _sections = new SparseArray<Section>();


    public SimpleSectionedRecyclerViewAdapter(Context context, int sectionResourceId, int textResourceId,
                                              RecyclerView.Adapter baseAdapter) {

        _layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _sectionResourceId = sectionResourceId;
        _textResourceId = textResourceId;
        _baseAdapter = baseAdapter;
        _context = context;

        _baseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                _valid = _baseAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                _valid = _baseAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionToSectionedPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                _valid = _baseAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionToSectionedPosition(positionStart), itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                _valid = _baseAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionToSectionedPosition(positionStart), itemCount);
            }
        });
    }


    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public SectionViewHolder(View view,int mTextResourceid) {
            super(view);
            title = (TextView) view.findViewById(mTextResourceid);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(_context).inflate(_sectionResourceId, parent, false);
            return new SectionViewHolder(view, _textResourceId);
        }else{
            return _baseAdapter.onCreateViewHolder(parent, typeView -1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder)sectionViewHolder).title.setText(_sections.get(position).title);
        }else{
            _baseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : _baseAdapter.getItemViewType(sectionedPositionToPosition(position)) +1 ;
    }


    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;

        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }
    }


    public void setSections(List<Section> sections) {
        _sections.clear();

        Collections.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            _sections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < _sections.size(); i++) {
            if (_sections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < _sections.size(); i++) {
            if (_sections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return _sections.get(position) != null;
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - _sections.indexOfKey(position)
                : _baseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    @Override
    public int getItemCount() {
        return (_valid ? _baseAdapter.getItemCount() + _sections.size() : 0);
    }

}
