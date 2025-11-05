package kevin.intellsoft.mediapp.ui.patient;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import kevin.intellsoft.mediapp.R;
import kevin.intellsoft.mediapp.data.local.AppDatabase;
import kevin.intellsoft.mediapp.data.local.entity.Patient;
import kevin.intellsoft.mediapp.util.DateUtils;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.VH> {

    private final List<Patient> items = new ArrayList<>();
    private final OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
    }

    public PatientAdapter(OnPatientClickListener listener) {
        this.listener = listener;
    }

    public void update(List<Patient> newList) {
        items.clear();
        if (newList != null) items.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_patient, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Patient p = items.get(position);
        holder.tvName.setText(p.getFullName());
        holder.tvAge.setText("Age: " + DateUtils.calculateAgeYears(p.dateOfBirth));

        // Set temporary loading text
        holder.tvBmiStatus.setText("Loading...");
        holder.tvBmiStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));

        // Run BMI query in background
        Executors.newSingleThreadExecutor().execute(() -> {
            Double latestBmi = AppDatabase.getInstance(holder.itemView.getContext())
                    .vitalDao()
                    .getLatestBmiForPatient(p.patientId);

            // Update UI safely on main thread
            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                if (latestBmi != null) {
                    String bmiStatus;
                    int bmiColor;

                    if (latestBmi < 18.5) {
                        bmiStatus = "Underweight";
                        bmiColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.red);
                    } else if (latestBmi < 25) {
                        bmiStatus = "Normal";
                        bmiColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
                    } else {
                        bmiStatus = "Overweight";
                        bmiColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.orange);
                    }

                    holder.tvBmiStatus.setText(bmiStatus);
                    holder.tvBmiStatus.setTextColor(bmiColor);
                } else {
                    holder.tvBmiStatus.setText("No Data");
                    holder.tvBmiStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
                }
            });
        });

        holder.itemView.setOnClickListener(v -> listener.onPatientClick(p));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvAge, tvBmiStatus;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvAge = v.findViewById(R.id.tvAge);
            tvBmiStatus = v.findViewById(R.id.tvBmiStatus);
        }
    }
}
