package net.artux.pda.ui.fragments.stories

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import net.artux.pda.R
import net.artux.pda.databinding.FragmentListBinding
import net.artux.pda.model.StatusModel
import net.artux.pda.model.StoryAlert
import net.artux.pda.model.StorySelector
import net.artux.pda.model.quest.StoryItem
import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.ui.activities.QuestActivity
import net.artux.pda.ui.activities.hierarhy.BaseFragment
import net.artux.pda.ui.fragments.stories.StoriesAdapter.OnStoryClickListener
import net.artux.pda.ui.viewmodels.StoriesViewModel

@AndroidEntryPoint
class StoriesFragment : BaseFragment(), OnStoryClickListener {
    private lateinit var binding: FragmentListBinding
    private val storiesViewModel: StoriesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationPresenter.setTitle(resources.getString(R.string.map))
        val adapter = StoriesAdapter(this)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter

        storiesViewModel.stories.observe(viewLifecycleOwner) { stories: List<StoryItem?> ->
            navigationPresenter.setLoadingState(false)
            if (stories.isNotEmpty()) {
                binding.list.visibility = View.VISIBLE
                binding.viewMessage.visibility = View.GONE
                adapter.setStories(stories)
            } else {
                binding.list.visibility = View.GONE
                binding.viewMessage.visibility = View.VISIBLE
            }
        }
        storiesViewModel.status.observe(viewLifecycleOwner) { statusModel: StatusModel ->
            Toast.makeText(requireContext(), statusModel.description, Toast.LENGTH_SHORT)
                .show()
        }
        navigationPresenter.setLoadingState(true)
        storiesViewModel.storyData.observe(viewLifecycleOwner) { memberResult: StoryDataModel ->
            val state = memberResult.currentState
            if (state == null) {
                storiesViewModel.updateStories()
                return@observe
            }

            state.let {
                storiesViewModel.selectStory(it.storyId, it.chapterId, it.stageId, it.current)
            }
        }
        storiesViewModel.storyAlert.observe(viewLifecycleOwner) { storyAlert: StoryAlert ->
            val builder = AlertDialog.Builder(activity, R.style.PDADialogStyle)
            builder.setTitle(storyAlert.titleId)
            builder.setMessage(storyAlert.messageId)
            builder.setNeutralButton(R.string.okay) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            if (storyAlert === StoryAlert.ALREADY_COMPLETE)
                builder.setNegativeButton(R.string.reset_story) { _: DialogInterface?, _: Int ->
                    storiesViewModel.resetSingleStory(storiesViewModel.selectedStoryId)
                }
            builder.show()
        }
        storiesViewModel.storySelector.observe(viewLifecycleOwner) { (storyId, chapterId, stageId, isCurrent): StorySelector ->
            val intent = Intent(activity, QuestActivity::class.java)
            intent.putExtra("current", isCurrent)
            intent.putExtra("storyId", storyId)
            intent.putExtra("chapterId", chapterId)
            intent.putExtra("stageId", stageId)
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }
        storiesViewModel.updateData()
    }

    override fun onClick(storyItem: StoryItem) {
        val id = storyItem.id
        if (id == -1) {
            val builder = AlertDialog.Builder(activity, R.style.PDADialogStyle)
            builder.setTitle(R.string.inputFormatToLoadStory)
            val input = EditText(activity)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(R.string.load) { _: DialogInterface?, _: Int ->
                val keys = input.text.toString().replace("[^0-9]+".toRegex(), ":").split(":".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()

                if (keys.size != 3) {
                    Toast.makeText(activity, getString(R.string.enterNotThreeValues), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                try {
                    val scs = intArrayOf(keys[0].toInt(), keys[1].toInt(), keys[2].toInt())
                    storiesViewModel.selectStory(scs[0], scs[1], scs[2])
                } catch (e: NumberFormatException) {
                    Toast.makeText(activity, getString(R.string.enterNotNumber), Toast.LENGTH_SHORT)
                        .show()
                }
            }
            builder.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            builder.show()
        } else if (id > -1) {
            storiesViewModel.selectStory(storyItem)
        }
    }
}